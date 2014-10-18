//
//  ViewController.m
//  HackTM
//
//  Created by Stefan Iarca on 10/15/14.
//  Copyright (c) 2014 Stefan Iarca. All rights reserved.
//

#import "ViewController.h"

#define kKeyboardAnimationDuration 0.3
#define ModifyValue 100

@interface ViewController () <UITextViewDelegate,UIPickerViewDelegate,UIPickerViewDataSource>

@property (nonatomic,strong) NSString *name;
@property (nonatomic,strong) NSString *surname;
@property (nonatomic,strong) UIImage *image;
@property (nonatomic,strong) NSString *phoneNumber;
@property (weak, nonatomic) IBOutlet UISwitch *smokingSwitch;
@property (weak, nonatomic) IBOutlet UITextView *textView;
@property (weak, nonatomic) IBOutlet UILabel *dayLabel;
@property (weak, nonatomic) IBOutlet UILabel *hourLabel;
@property (nonatomic) BOOL keyboardIsShown;
@property (weak, nonatomic) IBOutlet UIPickerView *pickerView;
@property (weak, nonatomic) IBOutlet UIPickerView *pickerViewHourMinute;

@property (nonatomic,strong) NSArray *possibleDaysArray;
@property (nonatomic,strong) NSMutableArray *possibleHoursArray;
@property (nonatomic,strong) NSMutableArray *possibleMinutesArray;

@property (weak, nonatomic) IBOutlet UIView *paravan;

@property (nonatomic,strong) NSString *selectedHour;
@property (nonatomic,strong) NSString *selectedMinutes;

@end

@implementation ViewController

-(NSArray *)possibleDaysArray{
    if (!_possibleDaysArray) {
        _possibleDaysArray = @[@"Today",@"Tomorrow"];
    }
    return _possibleDaysArray;
}

-(NSArray *)possibleHoursArray{
    if (!_possibleHoursArray) {
        _possibleHoursArray = [[NSMutableArray alloc]init];
        
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"HH"];
        int hour = [[dateFormatter stringFromDate:[NSDate date]] intValue];
        
        for(int i = hour; i <= 24; i++){
            [_possibleHoursArray addObject:[NSString stringWithFormat:@"%d",i]];
        }
    }
    return _possibleHoursArray;
}

-(NSArray *)possibleMinutesArray{
    if (!_possibleMinutesArray) {
        _possibleMinutesArray = [[NSMutableArray alloc]init];
        
        for(int i = 0; i < 60; i+=5){
            [_possibleMinutesArray addObject:[NSString stringWithFormat:@"%d",i]];
        }
    }
    return _possibleMinutesArray;
}

-(NSMutableArray *)recreatePossibleMinutesArray{
    NSMutableArray *array = [[NSMutableArray alloc]init];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"HH"];
    int hour = [[dateFormatter stringFromDate:[NSDate date]] intValue];
    NSLog(@"%@,%@",[NSString stringWithFormat:@"%d",hour],self.selectedHour);
    if ([[NSString stringWithFormat:@"%d",hour] isEqualToString:self.selectedHour]) {
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"mm"];
        int minutes = [[dateFormatter stringFromDate:[NSDate date]] intValue];
        
        for(int i = minutes; i < 60; i+=5){
            [array addObject:[NSString stringWithFormat:@"%d",i]];
        }
    }else{
        for(int i = 0; i < 60; i+=5){
            [array addObject:[NSString stringWithFormat:@"%d",i]];
        }
    }
    return array;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib
    self.navigationController.navigationBarHidden = YES;
    
    self.smokingSwitch.on = NO;
    self.textView.delegate = self;
    
    self.paravan.hidden = YES;
    
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"HH"];
    int hour = [[dateFormatter stringFromDate:[NSDate date]] intValue];
    
    self.selectedHour = [NSString stringWithFormat:@"%d",hour];
    
    self.pickerView.hidden = YES;
    self.pickerView.dataSource = self;
    self.pickerView.delegate = self;
    
    self.pickerViewHourMinute.delegate = self;
    self.pickerViewHourMinute.dataSource = self;
    self.pickerViewHourMinute.hidden = YES;
    
    [self registerForKeyboardNotifications];
}

-(void)viewWillAppear:(BOOL)animated{
    [self loadPreferences];
    [self getCurrentDate];
}

#pragma mark - DateFormatter

-(void)getCurrentDate{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    
    [dateFormatter setDateFormat:@"dd"];
    int day = [[dateFormatter stringFromDate:[NSDate date]] intValue];

    
    NSString *MyString;
    NSDate *now = [NSDate date];
    NSDateFormatter *dateFormatter2 = [[NSDateFormatter alloc] init];
    [dateFormatter2 setTimeStyle:NSDateFormatterShortStyle];
    MyString = [dateFormatter2 stringFromDate:now];
    
    self.dayLabel.text = [self stringFromDay:day];
    self.hourLabel.text = MyString;
}



-(NSString *)stringFromDay:(NSInteger)day{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"dd"];
    int actualDay = [[dateFormatter stringFromDate:[NSDate date]] intValue];
    
    if ((day - actualDay) == 0) {
        return @"Today";
    }else if((day - actualDay) == 0){
        return @"Tomorrow";
    }
    return @"Toyota";
}

#pragma mark - Load Preferences

-(void)loadPreferences{
    self.surname = [[NSUserDefaults standardUserDefaults] objectForKey:@"surname"];
    self.name = [[NSUserDefaults standardUserDefaults] objectForKey:@"name"];
    self.phoneNumber = [[NSUserDefaults standardUserDefaults] objectForKey:@"cellphoneNumber"];
    
    NSData *pngData = [NSData dataWithContentsOfFile:[self documentsPathForFileName:@"profileImage.png"]];
    UIImage *image = [UIImage imageWithData:pngData];
    
    if (image) {
        self.image = image;
    }else{
        self.image = [UIImage imageNamed:@"questionMark.jpg"];
    }
    [self checkNeededName:self.name andSurname:self.surname];
    
}

-(void)checkNeededName:(NSString *)name andSurname:(NSString *)surname{
    if (!(name)|| ([name isEqualToString:@""])) {
        [self showAlert];
        return;
    }
    if (!(surname)|| ([surname isEqualToString:@""])) {
        [self showAlert];
        return;
    }
}



#pragma Mark - Alert

-(void)showAlert{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Incomplete Profile"
                                                    message:@"You must enter your name and surname before you can ask for a driver"
                                                   delegate:self
                                          cancelButtonTitle:@"Complete"
                                          otherButtonTitles:@"Complete Later",nil];
    
    [alert show];
}


- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    [self performSegueWithIdentifier:@"prefSegue" sender:self];
}

#pragma mark - UITapGestureViewRecognizers

- (IBAction)changeDay:(UITapGestureRecognizer *)sender {
    self.pickerView.hidden = NO;
    self.paravan.hidden = NO;
    [self.view bringSubviewToFront:self.paravan];
    [self.view bringSubviewToFront:self.pickerView];
}

- (IBAction)changeHour:(UITapGestureRecognizer *)sender {

    self.pickerViewHourMinute.hidden = NO;
    self.paravan.hidden = NO;
    [self.view bringSubviewToFront:self.paravan];
    [self.view bringSubviewToFront:self.pickerViewHourMinute];
}

- (IBAction)hidePickerViews {
    self.pickerView.hidden = YES;
    self.pickerViewHourMinute.hidden = YES;
    self.paravan.hidden = YES;
}

- (IBAction)attachLocationAndGo {
    [self performSegueWithIdentifier:@"MapSegue" sender:self];
}

#pragma mark - TextView delegate

-(BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text
{
    if([text isEqualToString:@"\n"])
        [textView resignFirstResponder];
    return YES;
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

#pragma mark - PickerView delegate

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component{
    if (pickerView.tag == 0) {
        return self.possibleDaysArray[row];
    }else{
        if (component == 0) {
            return self.possibleHoursArray[row];
        }else{
            return self.possibleMinutesArray[row];
        }
    }
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component{
    if (pickerView.tag == 0) {
        self.dayLabel.text = self.possibleDaysArray[row];
    }else{
        if (component == 0) {
            self.selectedHour = self.possibleHoursArray[row];
        }else{
            self.selectedMinutes = self.possibleMinutesArray[row];
        }
        [self setPickerViewRowHour];
    }
}


#pragma mark - PickerView dataSource
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView{
    if(pickerView.tag == 0){
        return 1;
    }else{
        return 2;
    }
}

// returns the # of rows in each component..
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component{
    if (pickerView.tag == 0) {
        return [self.possibleDaysArray count];
    }else{
        if (component == 0) {
            return [self.possibleHoursArray count];
        }else{
            return [self.possibleMinutesArray count];
        }
    }
}

-(void)setPickerViewRowHour{
    self.hourLabel.text = [NSString stringWithFormat:@"%@:%@",self.selectedHour,self.selectedMinutes];
    self.possibleMinutesArray = [self recreatePossibleMinutesArray];
}


#pragma mark - File Path Getter
- (NSString *)documentsPathForFileName:(NSString *)name
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask, YES);
    NSString *documentsPath = [paths objectAtIndex:0];
    
    return [documentsPath stringByAppendingPathComponent:name];
}

@end
