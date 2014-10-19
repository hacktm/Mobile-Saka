//
//  AppDelegate.m
//  HackTM
//
//  Created by Stefan Iarca on 10/15/14.
//  Copyright (c) 2014 Stefan Iarca. All rights reserved.
//

#import "AppDelegate.h"
#import <GoogleMaps/GoogleMaps.h>

@interface AppDelegate () <NSURLConnectionDelegate>
@property (nonatomic,strong) NSURLConnection *connection;
@property (nonatomic,strong) NSMutableData *receivedData;
@property (nonatomic,strong) UIActivityIndicatorView *activityView;
@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    if ([self checkFirstTime]) {
        if (![[NSUserDefaults standardUserDefaults] boolForKey:@"registerMessageShow"]) {
            [self showRegisterMessage];
        }else{
            [self showCodeCheckMessageWithPhoneNumber:[[NSUserDefaults standardUserDefaults] objectForKey:@"cellphoneNumber"]];
        }
    }
    [GMSServices provideAPIKey:@"AIzaSyAOYlSwhovhXlF-otR5FZ6-Gvo_jfjoa2E"];
    return YES;
}

#pragma Mark - Alert

-(void)showRegisterMessage{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Registration"
                                                    message:@"Before you can use the app, you have to register using your phone number."
                                                   delegate:self
                                          cancelButtonTitle:@"Register"
                                          otherButtonTitles:nil];
    alert.alertViewStyle = UIAlertViewStylePlainTextInput;
    
    [alert show];
}

-(void)showCodeCheckMessageWithPhoneNumber:(NSString *)number{
    NSString *alertMessage =  [NSString stringWithFormat:@"A text message with a verification code was sent to: %@. Enter the code here.",number];
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Check code"
                                                    message:alertMessage
                                                   delegate:self
                                          cancelButtonTitle:@"Enter"
                                          otherButtonTitles:nil];
  
    alert.alertViewStyle = UIAlertViewStylePlainTextInput;
    
    [alert show];
}

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    
    UITextField *alertTextField = [alertView textFieldAtIndex:0];
    
    if ([alertView.title isEqualToString:@"Registration"]) {
        if (buttonIndex == 0) {
            NSLog(@"Send number : %@ to server",alertTextField.text);
            [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"registerMessageShow"];
            [[NSUserDefaults standardUserDefaults] setObject:alertTextField.text forKey:@"cellphoneNumber"];
            [self showCodeCheckMessageWithPhoneNumber:alertTextField.text];
        }
    }else{
        if (buttonIndex == 0) {
            NSLog(@"Code: %@",alertTextField.text);
            [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"defaultsSet"];
            [self setFirstDefaults];
        }
    }

}

#pragma mark - Dealing with Server

-(void)sendPhoneNumberRequestWithNumber:(NSString *)number{
    NSString *ip = @"localhost";
    NSString *url = [NSString stringWithFormat:@"%@/hacktm/insert_valid.php",ip];

    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    [request setURL:[NSURL URLWithString:url]];
    request.HTTPMethod = @"POST";
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];

    NSString *stringData = [NSString stringWithFormat:@"phone=%@",number];
    NSData *requestBodyData = [stringData dataUsingEncoding:NSUTF8StringEncoding];
    request.HTTPBody = requestBodyData;

    NSURLConnection *connection = [[NSURLConnection alloc] initWithRequest:request delegate:self];
    [connection start];
}

-(void)verifyCode:(NSString *)code withNumber:(NSString *)number{
    NSString *ip = @"localhost";
    NSString *url = [NSString stringWithFormat:@"%@/hacktm/validate",ip];
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    [request setURL:[NSURL URLWithString:url]];
    request.HTTPMethod = @"POST";
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    
    NSString *stringData = [NSString stringWithFormat:@"phone=%@&code=%@",number,code];
    NSData *requestBodyData = [stringData dataUsingEncoding:NSUTF8StringEncoding];
    request.HTTPBody = requestBodyData;
    
    self.connection = [[NSURLConnection alloc] initWithRequest:request delegate:self];
    [self.connection start];
    
    [self stopActivityIndicator];
}

#pragma mark - URL Connection Delegate

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    // This method is called when the server has determined that it
    // has enough information to create the NSURLResponse object.
    
    // It can be called multiple times, for example in the case of a
    // redirect, so each time we reset the data.
    
    // receivedData is an instance variable declared elsewhere.
    [self.receivedData setLength:0];
    
    NSLog(@"%@",response);
    
    [self stopActivityIndicator];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // Append the new data to receivedData.
    // receivedData is an instance variable declared elsewhere.
    [self.receivedData appendData:data];
}

- (void)connection:(NSURLConnection *)connection
  didFailWithError:(NSError *)error
{
    // Release the connection and the data object
    // by setting the properties (declared elsewhere)
    // to nil.  Note that a real-world app usually
    // requires the delegate to manage more than one
    // connection at a time, so these lines would
    // typically be replaced by code to iterate through
    // whatever data structures you are using.
    self.connection = nil;
    self.receivedData = nil;
    
    // inform the user
    NSLog(@"Connection failed! Error - %@ %@",
          [error localizedDescription],
          [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // do something with the data
    // receivedData is declared as a property elsewhere
    NSLog(@"Succeeded! Received %d bytes of data",[self.receivedData length]);
    
    // Release the connection and the data object
    // by setting the properties (declared elsewhere)
    // to nil.  Note that a real-world app usually
    // requires the delegate to manage more than one
    // connection at a time, so these lines would
    // typically be replaced by code to iterate through
    // whatever data structures you are using.
    self.connection = nil;
    self.receivedData = nil;
}

#pragma mark - Activity Indicator

-(void)presentActivityIndicator{
    self.activityView =[[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    
    self.activityView.center= self.window.rootViewController.view.center;
    
    [self.activityView startAnimating];
    
    [self.window.rootViewController.view addSubview:self.activityView];
}

-(void)stopActivityIndicator{
    [self.activityView stopAnimating];
}

#pragma mark - User Defaults

-(void)setFirstDefaults{
    [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"defaultsSet"];
    [[NSUserDefaults standardUserDefaults] setObject:@"" forKey:@"surname"];
    [[NSUserDefaults standardUserDefaults] setObject:@"" forKey:@"name"];
}

-(BOOL)checkFirstTime{
    if ([[NSUserDefaults standardUserDefaults] boolForKey:@"defaultsSet"]) {
        return NO;
    }
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
