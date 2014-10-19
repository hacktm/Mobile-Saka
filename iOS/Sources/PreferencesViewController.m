//
//  PreferencesViewController.m
//  HackTM
//
//  Created by Stefan Iarca on 10/18/14.
//  Copyright (c) 2014 Stefan Iarca. All rights reserved.
//

#import "PreferencesViewController.h"

#define kKeyboardAnimationDuration 0.3
#define ModifyValue 100

@interface PreferencesViewController () <UITextFieldDelegate,UINavigationControllerDelegate,UIImagePickerControllerDelegate>

@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UITextField *nameTextField;
@property (weak, nonatomic) IBOutlet UITextField *surnameTextField;
@property (weak, nonatomic) IBOutlet UILabel *phoneNumberLabel;
@property (nonatomic) BOOL keyboardIsShown;

@end

@implementation PreferencesViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self loadPref];
    [self registerForKeyboardNotifications];
    [self registerForTextfieldNotification];
}

#pragma mark - Loading existing preferences
-(void)loadPref{
    self.surnameTextField.text = [[NSUserDefaults standardUserDefaults] objectForKey:@"surname"];
    self.nameTextField.text = [[NSUserDefaults standardUserDefaults] objectForKey:@"name"];
    self.phoneNumberLabel.text = [[NSUserDefaults standardUserDefaults] objectForKey:@"cellphoneNumber"];
    
    NSData *pngData = [NSData dataWithContentsOfFile:[self documentsPathForFileName:@"profileImage.png"]];
    UIImage *image = [UIImage imageWithData:pngData];
    
    if (image) {
        self.imageView.image = image;
    }
    
}

#pragma mark - TextField delegate

-(void)registerForTextfieldNotification{
    self.surnameTextField.delegate = self;
    self.nameTextField.delegate = self;
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField{
    [textField resignFirstResponder];
    return YES;
}

- (BOOL)textField:(UITextField *)theTextField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSCharacterSet *myCharSet;
    myCharSet = [NSCharacterSet characterSetWithCharactersInString:@"QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm"];
    
    for (int i = 0; i < [string length]; i++) {
        unichar c = [string characterAtIndex:i];
        if (![myCharSet characterIsMember:c]) {
            return NO;
        }
    }
    
    return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField{
    if ([textField isEqual:self.surnameTextField]) {
        [[NSUserDefaults standardUserDefaults] setObject:textField.text forKey:@"surname"];
    }else{
        [[NSUserDefaults standardUserDefaults] setObject:textField.text forKey:@"name"];
    }
}

#pragma mark - Keyboard logic

-(void)registerForKeyboardNotifications{
    // register for keyboard notifications
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillShow:)
                                                 name:UIKeyboardWillShowNotification
                                               object:self.view.window];
    // register for keyboard notifications
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:self.view.window];
    
    self.keyboardIsShown = NO;
}

- (void)keyboardWillShow:(NSNotification *)n{
    
    if (self.keyboardIsShown)
    {
        return;
    }
    
    CGRect viewFrame = self.view.frame;
    viewFrame.origin.y-= ModifyValue;
    
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationDuration:kKeyboardAnimationDuration];
    [self.view setFrame:viewFrame];
    [UIView commitAnimations];
    
    [[UIApplication sharedApplication] setStatusBarHidden:YES withAnimation:UIStatusBarAnimationSlide];
    
    self.keyboardIsShown = YES;
}


- (void)keyboardWillHide:(NSNotification *)n{
    
    CGRect viewFrame = self.view.frame;
    viewFrame.origin.y+= ModifyValue;
    
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationDuration:kKeyboardAnimationDuration];
    [self.view setFrame:viewFrame];
    [UIView commitAnimations];
    
    [[UIApplication sharedApplication] setStatusBarHidden:NO withAnimation:UIStatusBarAnimationSlide];
    
    self.keyboardIsShown = NO;
}


#pragma mark - UITapGestureRecognizer 

- (IBAction)changeImage:(UITapGestureRecognizer *)sender {
    
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera] == NO){
        return;
    }
    
    UIImagePickerController *imagePicker = [[UIImagePickerController alloc]init];
   
    if ([UIImagePickerController isCameraDeviceAvailable:UIImagePickerControllerCameraDeviceFront]) {
        imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
        imagePicker.cameraDevice = UIImagePickerControllerCameraDeviceFront;
    }else{
        imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    }
    
    imagePicker.allowsEditing = YES;
    imagePicker.delegate = self;
    [self presentViewController:imagePicker animated:YES completion:^{
        NSLog(@"Picking Photo...");
        }
    ];
}

- (void)imagePickerController:(UIImagePickerController *)picker
didFinishPickingMediaWithInfo:(NSDictionary *)info{
    UIImage *image = [info objectForKey:UIImagePickerControllerEditedImage];
    NSLog(@"%@",image);
    self.imageView.image = image;
    [self dismissViewControllerAnimated:YES completion:^{
        NSData *pngData = UIImagePNGRepresentation(image);
        
        NSString *filePath = [self documentsPathForFileName:@"profileImage.png"];
        [pngData writeToFile:filePath atomically:YES];
    }
    ];
}

#pragma mark - File Path Getter
- (NSString *)documentsPathForFileName:(NSString *)name
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask, YES);
    NSString *documentsPath = [paths objectAtIndex:0];
    
    return [documentsPath stringByAppendingPathComponent:name];
}

@end
