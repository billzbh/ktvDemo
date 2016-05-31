//
//  WorkingViewController.m
//  ktvDEMO
//
//  Created by zbh on 16/4/5.
//  Copyright © 2016年 hxsmart. All rights reserved.
//

#import "WorkingViewController.h"
#import "DLRadioButton.h"
#import "TRSDialScrollView.h"
#import "GCDAsyncSocket.h"
#import "ZBHAlertViewController.h"
#import "ZBInputAlertViewController.h"
#import "JDFTooltips.h"
#import "JDFSequentialTooltipManager.h"

#define isPad (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad)
#define kGreenColor [UIColor colorWithRed:144/255.0 green: 202/255.0 blue: 119/255.0 alpha: 1.0]

#define PORT 8080
#define DEFAULTSETTING @"LIUJIEWEN"
#define DEFAULTSETTING_BAK @"LIUJIEWEN_BAK"

static WorkingViewController *sg_WorkingController = nil;

static Byte sendBytes2[2] = {0x00,0x00};
static Byte sendBytes3[3] = {0x00,0x00,0x00};
static Byte GoLeft[2]= {0x9A,0xEA};//左箭头
static Byte GoRight[2] = {0x9A,0xED};//右箭头

static Byte LeftsendUP[2]= {0x9A,0xDD};
static Byte LeftsendDown[2] = {0x9A,0xDC};

static Byte RightsendUP[2]= {0x9A,0xDA};
static Byte RightsendDown[2] = {0x9A,0xDB};

@interface WorkingViewController () <UIPickerViewDataSource,UIPickerViewDelegate,TRSDialScrollViewSlipDirectionDelegate>
{
    NSMutableArray *Gun1;
    NSMutableArray *Gun2;
    NSMutableArray *Gun3;
    int whichPage;
    int GValue;
    CGPoint startTouchPosition;
    float last;
    float lastY;
    BOOL isLockMode;
    BOOL initFlag;
    GCDAsyncSocket* Socket;
    NSString *IP;
    NSInteger CHANNEL;
    
}
@property (strong,nonatomic) NSUserDefaults* defaults;
@property (strong,nonatomic) NSMutableDictionary* dict;
@property (strong,nonatomic) NSMutableDictionary* dict_bak;

@property (nonatomic, strong) JDFSequentialTooltipManager *tooltipManager;

@property (weak, nonatomic) IBOutlet UIPickerView *whichG;
@property (weak, nonatomic) IBOutlet UIView *topview;
@property (weak, nonatomic) IBOutlet UIView *middleView;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) IBOutlet UIView *LeftView;
@property (weak, nonatomic) IBOutlet UIView *RigthView;
@property (strong,nonatomic) TRSDialScrollView* leftWheel;
@property (strong,nonatomic) TRSDialScrollView* rightWheel;

@property (strong, nonatomic) IBOutletCollection(UIButton) NSArray *fifteenButtons;


@property (strong, nonatomic) IBOutletCollection(UISlider) NSArray *fifteenSliders;

@property (strong, nonatomic) NSMutableArray *longPressObjects;

@property (strong, nonatomic) IBOutletCollection(UILabel) NSArray *SliderValueText;

@property (weak, nonatomic) IBOutlet UIButton *resetAllSliderValue;

@end

@implementation WorkingViewController


- (IBAction)leftbutton:(id)sender {
    [self sendBytes2:GoLeft];
}

- (IBAction)rightbutton:(id)sender {
    [self sendBytes2:GoRight];
}


-(void)awakeFromNib{
    sg_WorkingController = self;
}

+(instancetype)shareController
{
    return sg_WorkingController;
}

- (IBAction)selectChannel:(DLRadioButton*)radiobutton {

    
    int channel = [radiobutton.selectedButton.titleLabel.text intValue];
//    NSLog(@"选择了%d页\n", channel);
    whichPage = channel;
    
    if (whichPage==1) {
        sendBytes2[1]= GValue;
    }else if(whichPage == 2)
    {
        sendBytes2[1]= GValue+10;
    }else if(whichPage==3)
    {
        sendBytes2[1]= GValue+20;
    }
    [self sendBytes2:sendBytes2];
    
    [self updateGunArray];
    
    
    //TODO 暂时不更新 slider的值
    if (![Socket isConnected]) {
        [self updateAllseekbar];
    }
}

- (void)viewDidAppear:(BOOL)animated
{
    _leftWheel = [[TRSDialScrollView alloc] initWithFrame:_LeftView.bounds];
    _rightWheel = [[TRSDialScrollView alloc] initWithFrame:_RigthView.bounds];
    [_LeftView addSubview:_leftWheel];
    [_RigthView addSubview:_rightWheel];
    
    _leftWheel.SlipDirectiondelegate = self;
    _rightWheel.SlipDirectiondelegate = self;
    _leftWheel.tag = -1;
    _rightWheel.tag = 1;
    
    [_scrollView setContentSize:CGSizeMake(_scrollView.bounds.size.width*3,_scrollView.bounds.size.height)];
//    NSLog(@"%f",_scrollView.contentSize.width);
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    Socket = [GCDAsyncSocket sharedController];
    [Socket setFlag:-10];
    [Socket setDelegate:self];
    _defaults = [NSUserDefaults standardUserDefaults];
    IP = [self.defaults objectForKey:@"IP"];
    CHANNEL = [_defaults integerForKey:@"CHANNEL"];
    _dict_bak = [[NSMutableDictionary alloc] initWithCapacity:450];
    
    // Do any additional setup after loading the view.
    whichPage = 1;
    GValue = 1;
    
    Gun1 = [[NSMutableArray alloc] initWithArray:@[@"1 A",@"1 B",@"1 C",@"1 D",@"1 E",@"1 F",@"1 G",@"1 H",@"1 I",@"1 J"]];
    Gun2 = [[NSMutableArray alloc] initWithArray:@[@"2 A",@"2 B",@"2 C",@"2 D",@"2 E",@"2 F",@"2 G",@"2 H",@"2 I",@"2 J"]];
    Gun3 = [[NSMutableArray alloc] initWithArray:@[@"3 A",@"3 B",@"3 C",@"3 D",@"3 E",@"3 F",@"3 G",@"3 H",@"3 I",@"3 J"]];
    
    
    [_whichG setDataSource:self];
    [_whichG setDelegate:self];
    isLockMode = YES;
    initFlag = YES;
    _longPressObjects = [[NSMutableArray alloc] initWithCapacity:15];
    
    //初始化，自动转一圈，避免第一次是数组第一个值造成留白
    [_whichG selectRow:[Gun1 count]*4000 inComponent:0 animated:YES];
    
    CGColorSpaceRef colorSpaceRef = CGColorSpaceCreateDeviceRGB();
    CGColorRef color = CGColorCreate(colorSpaceRef, (CGFloat[]){0,0,0,1});
    [_scrollView.layer setBorderWidth:1];
    [_scrollView.layer setBorderColor:color];
    [self.view.layer setBorderWidth:1];
    [self.view.layer setBorderColor:color];
    
    
    //信号通道的值
    sendBytes3[0]= (Byte) (0x90 | (Byte)CHANNEL);
    sendBytes2[0] = (Byte) (0xc0 | (Byte)CHANNEL);
    
    sendBytes2[1]= (Byte)0x01;
    [self sendBytes2:sendBytes2];
    
    [self init15Buttons];
    [self init15Slider];
    [self updateAllbtnText];
    [self updateGunArray];
}

-(void)viewDidDisappear:(BOOL)animated{
    [Socket disconnect];
    [_defaults setObject:_dict forKey:DEFAULTSETTING];
    [_defaults setObject:_dict_bak forKey:DEFAULTSETTING_BAK];
}

-(void)updateGunArray{
    
    NSString *KEY;
    for (int j = 1; j <= 10; j++) {
        KEY = [NSString stringWithFormat:@"%d",whichPage*10+j];
        NSString *value = [_defaults objectForKey:KEY];
        if (value) {
            if (whichPage==1) {
                [Gun1 replaceObjectAtIndex:j-1 withObject:value];
            }else if (whichPage==2){
                [Gun2 replaceObjectAtIndex:j-1 withObject:value];
            }else if (whichPage == 3)
            {
                [Gun3 replaceObjectAtIndex:j-1 withObject:value];
            }
        }
        [_whichG reloadComponent:0];
    }
}

-(void)init15Slider{
    
    //底下背景图 和 滑块图
//    UIImage *stetchTrack = [[UIImage imageNamed:@"faderTrack.png"] stretchableImageWithLeftCapWidth:10.0 topCapHeight:0.0];
    UIImage *ThumbImage = [UIImage imageNamed:@"light_slide_button.png"];
    NSString *KEY;
    NSInteger Value;
    
    CGAffineTransform trans = CGAffineTransformMakeRotation(M_PI * -0.5);
    int i = 0;
    for (UISlider *slider in _fifteenSliders)
    {
        slider.transform = trans;
        [slider setTag:i];
        
        [slider addTarget:self action:@selector(SliderAction:) forControlEvents:UIControlEventValueChanged];
        slider.backgroundColor = [UIColor clearColor];
        
        [slider setThumbImage:ThumbImage forState:UIControlStateNormal];
//        [slider setMinimumTrackImage:stetchTrack forState:UIControlStateNormal];
//        [slider setMaximumTrackImage:stetchTrack forState:UIControlStateNormal];
        [slider setMaximumValue:255.0];
        [slider setMinimumValue:0.0];
        
        KEY = [NSString stringWithFormat:@"%d%02d",(whichPage*10+GValue),i+1];
        Value = [_defaults integerForKey:KEY];
        [slider setValue:Value];
        if (Value!=0) {
            sendBytes3[1]=(Byte)i;
            sendBytes3[2]=(Byte)(Value/2);
            [self sendBytes3:sendBytes3];
            
            [[_SliderValueText objectAtIndex:i] setText:[NSString stringWithFormat:@"%d",Value]];
        }
        
        i++;
    }
}



-(void)init15Buttons
{
    //初始化一个推杆置零按钮
    [_resetAllSliderValue.layer setMasksToBounds:YES];//设置按钮的圆角半径不会被遮挡
    [_resetAllSliderValue.layer setCornerRadius:4];
    [_resetAllSliderValue.layer setBorderWidth:1];//设置边界的宽度
    //设置按钮的边界颜色
    CGColorSpaceRef colorSpaceRef = CGColorSpaceCreateDeviceRGB();
    CGColorRef color = CGColorCreate(colorSpaceRef, (CGFloat[]){0,0.5,1,1});
    [_resetAllSliderValue.layer setBorderColor:color];
    
    //15个按钮初始化

    //iphone 显示12字体，10个汉字
    //ipad  显示18字体，18个汉字
    for (UIButton *button in _fifteenButtons) {
        if (!isPad) {
            button.titleLabel.font = [UIFont systemFontOfSize: 12.0];
        }
        
        [button.layer setMasksToBounds:YES];//设置按钮的圆角半径不会被遮挡
        [button.layer setCornerRadius:8];
        [button.layer setBorderWidth:1];//设置边界的宽度
        //设置按钮的边界颜色
        CGColorSpaceRef colorSpaceRef = CGColorSpaceCreateDeviceRGB();
        CGColorRef color = CGColorCreate(colorSpaceRef, (CGFloat[]){0,0.5,1,1});
        [button.layer setBorderColor:color];
        
        UILongPressGestureRecognizer *longPress=[[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(buttonLongPress:)];
        longPress.minimumPressDuration=0.6;//定义按的时间
        [button addGestureRecognizer:longPress];
        [_longPressObjects addObject:longPress];
    }
    
    
    UILongPressGestureRecognizer *longPressForGUN=[[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(changePageTip:)];
    longPressForGUN.minimumPressDuration=0.4;//定义按的时间
    [_whichG addGestureRecognizer:longPressForGUN];
}


- (void)SliderAction:(UISlider*)sender
{
    int tag = [sender tag];
    int value = (int)[sender value];
    [[_SliderValueText objectAtIndex:tag] setText:[NSString stringWithFormat:@"%d",value]];
    
    sendBytes3[1]=(Byte)tag;
    sendBytes3[2]=(Byte)(value/2);
    [self sendBytes3:sendBytes3];
    
    NSString* KEY = [NSString stringWithFormat:@"%d%02d",(whichPage*10+GValue),tag+1];
    [_defaults setInteger:value forKey:KEY];
    
}

- (IBAction)ModeSelect:(UIButton *)sender {
    
    BOOL isSelect =  [sender isSelected];
    [sender setSelected:!isSelect];
    if ([sender isSelected]) {
        //点控模式
//        NSLog(@"点控模式");
        isLockMode = NO;
        [self removeFifteenButtonsLongPress];
    }else{
        //锁定模式
//        NSLog(@"锁定模式");
        isLockMode = YES;
        [self addFifteenButtonsLongPress];
    }
    
}


- (void)addFifteenButtonsLongPress
{
    for (UIButton *button in _fifteenButtons){
        [button addGestureRecognizer:[_longPressObjects objectAtIndex:[button tag]]];
    }
}

-(void)removeFifteenButtonsLongPress
{
    for (UIButton *button in _fifteenButtons){
        [button removeGestureRecognizer:[_longPressObjects objectAtIndex:[button tag]]];
    }
}




- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

-(void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    int tag = [pickerView tag];
    
    if (tag == 1024) {//滚筒
        
        GValue =row%[Gun1 count] + 1;
//        NSLog(@"滚筒的值：%d",GValue);
        
        
        sendBytes2[1]=(Byte)(GValue + (whichPage-1)*10);
        [self sendBytes2:sendBytes2];
        
        //TODO 暂时不更新 slider的值
        if (![Socket isConnected]) {
            [self updateAllseekbar];
        }
      
        
        NSUInteger max = 0;
        NSUInteger base10 = 0;
        if(component == 0)
        {
            max = [Gun1 count]*5000;
            base10 = (max/2)-(max/2)%[Gun1 count];
            [pickerView selectRow:[pickerView selectedRowInComponent:component]%[Gun1 count]+base10 inComponent:component animated:false];
        }
    }
}

-(NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return [Gun1 count]*5000;
}


-(NSString*)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{

        if (whichPage==1) {
            return [Gun1 objectAtIndex:(row%[Gun1 count])];
        }else if (whichPage==2)
        {
            return [Gun2 objectAtIndex:(row%[Gun2 count])];
        }else if (whichPage==3)
        {
            return [Gun3 objectAtIndex:(row%[Gun3 count])];
        }
        return [Gun1 objectAtIndex:(row%[Gun1 count])];
}

-(CGFloat)pickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component
{
        if(isPad)
            return 40.0;
        else
            return 22.0;
}

-(UIView *)pickerView:(UIPickerView *)pickerView viewForRow:(NSInteger)row forComponent:(NSInteger)component reusingView:(UIView *)view
{
    UILabel* pickerLabel = (UILabel*)view;
    if (!pickerLabel){
        pickerLabel = [[UILabel alloc] init];
        // Setup label properties - frame, font, colors etc
        //adjustsFontSizeToFitWidth property to YES
        
        pickerLabel.adjustsFontSizeToFitWidth = YES;
        [pickerLabel setTextAlignment:NSTextAlignmentCenter];
        [pickerLabel setBackgroundColor:[UIColor clearColor]];
        if (isPad) {
            [pickerLabel setFont:[UIFont boldSystemFontOfSize:18]];
        }else{
            [pickerLabel setFont:[UIFont boldSystemFontOfSize:14]];
        }
        
    }
    // Fill the label text here
    pickerLabel.text=[self pickerView:pickerView titleForRow:row forComponent:component];
    return pickerLabel;

}


- (IBAction)resetAllSliderValue2Zero:(id)sender {
//    NSLog(@"清除一页的推杆");
    ZBHAlertViewController *alertView = [[ZBHAlertViewController alloc] initWithTitle:@"警告" message:@"是否这一页所有推杆置零?" viewController:self];
    
    RIButtonItem *cancelItem = [RIButtonItem itemWithLabel:@"取消" action:^{
        
    }];
    [alertView addButton:cancelItem type:RIButtonItemType_Cancel];
    
    RIButtonItem *okItem = [RIButtonItem itemWithLabel:@"确定" action:^{
        [self SliderResetZero];
    }];
    [alertView addButton:okItem type:RIButtonItemType_Destructive];
    [alertView show];
}

- (IBAction)resetOne:(UILongPressGestureRecognizer *)sender {
    
    //弹出对话框
    if([(UILongPressGestureRecognizer*)sender state] == UIGestureRecognizerStateBegan){
//        NSLog(@"重置所有页的推杆");
        ZBHAlertViewController *alertView = [[ZBHAlertViewController alloc] initWithTitle:@"警告" message:@"是否所有页的所有推杆置零?" viewController:self];
        
        RIButtonItem *cancelItem = [RIButtonItem itemWithLabel:@"取消" action:^{
            
        }];
        [alertView addButton:cancelItem type:RIButtonItemType_Cancel];
        
        RIButtonItem *okItem = [RIButtonItem itemWithLabel:@"确定" action:^{

            [self allSliderResetZero];
        }];
        [alertView addButton:okItem type:RIButtonItemType_Destructive];
        [alertView show];
    }
}

- (IBAction)changePageTip:(UILongPressGestureRecognizer *)sender {
    
    //弹出对话框
    if([(UILongPressGestureRecognizer*)sender state] == UIGestureRecognizerStateBegan){
//        NSLog(@"改变滚筒选中行的显示字符串");
        NSString *placeText;
        if (whichPage == 1) {
            placeText = [Gun1 objectAtIndex:GValue-1];
        }else if (whichPage == 2)
        {
            placeText = [Gun2 objectAtIndex:GValue-1];
        }else if (whichPage == 3)
        {
            placeText = [Gun3 objectAtIndex:GValue-1];
        }
        
        ZBInputAlertViewController *alertView = [[ZBInputAlertViewController alloc] initWithTitle:@"请输入新标识" message:nil TextFieldHint:@"" TextFieldcontentText:placeText viewController:self];
        
        [alertView showWithAction:^(NSString *PlainText) {
            
            NSString *subString;
            if(!isPad){
                if ([PlainText length]>8) {//中文最多8个
                    subString = [PlainText substringToIndex:8];
                }else{
                    subString = PlainText;
                }
            }else{
                if ([PlainText length]>14) {//中文最多10个
                    subString = [PlainText substringToIndex:14];
                }else{
                    subString = PlainText;
                }
            }
            
            
            if (whichPage==1) {
                [Gun1 replaceObjectAtIndex:GValue-1 withObject:subString];
            }else if (whichPage==2){
                [Gun2 replaceObjectAtIndex:GValue-1 withObject:subString];
            }else if (whichPage == 3)
            {
                [Gun3 replaceObjectAtIndex:GValue-1 withObject:subString];
            }
            
            [_whichG reloadComponent:0];

            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                //保存这个值到 _defaults
                NSString *KEY = [NSString stringWithFormat:@"%d",whichPage*10+GValue];
                [_defaults setObject:subString forKey:KEY];
            });
        }];
    }
}


- (void)buttonLongPress:(UILongPressGestureRecognizer *)sender {
    if([(UILongPressGestureRecognizer*)sender state] == UIGestureRecognizerStateBegan){
        
        int i = [[sender view] tag];
        ZBInputAlertViewController *alertView = [[ZBInputAlertViewController alloc] initWithTitle:@"请输入新标识" message:nil TextFieldHint:nil TextFieldcontentText:nil viewController:self];
        
        
        
        [alertView showWithAction:^(NSString *PlainText) {

            NSString* KEY = [NSString stringWithFormat:@"%d%02d",(whichPage*10+GValue),i+1];
            __block NSString *subString;
            
            dispatch_async(dispatch_get_main_queue(), ^{
                
                
//                if(isPad)
//                {
//                    
//                    [self charNumber:PlainText subString:&subString outLength:36];
//                }else{
//                    
//                    [self charNumber:PlainText subString:&subString outLength:36];
//                }
                
                if(isPad)
                {
                    if ([PlainText length]>18) {//中文最多18个
                        subString = [PlainText substringToIndex:28];
                    }else{
                        subString = PlainText;
                    }
                    
                }else{
                    
                    if ([PlainText length]>12) {//中文最多12个
                        subString = [PlainText substringToIndex:12];
                    }else{
                        subString = PlainText;
                    }
                }
                
                [((UIButton*)_fifteenButtons[i]) setTitle:subString forState:UIControlStateNormal];
                dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                    [_dict_bak setObject:subString forKey:KEY];
                    [_dict setObject:subString forKey:KEY];
                });
            });
        }];
    }
}



-(void)slipToUP:(BOOL)isUp tag:(int)tag
{
    if (tag==-1) {
    //左边
        if (isUp) {
//            NSLog(@"左边正在向上滑");
            for(int i=0;i<=8;i++)
                [self sendBytes2:LeftsendUP];
        }else{
//            NSLog(@"左边正在向下滑");
            for(int i=0;i<=8;i++)
                [self sendBytes2:LeftsendDown];
        }
    }else if(tag==1){
    //右边
        if (isUp) {
//            NSLog(@"右边正在向上滑");
            for(int i=0;i<=8;i++)
                [self sendBytes2:RightsendUP];
        }else{
//            NSLog(@"右边正在向下滑");
            for(int i=0;i<=8;i++)
                [self sendBytes2:RightsendDown];
        }
    }
}

- (IBAction)buttonAction:(UIButton *)sender forEvent:(UIEvent *)event {
    long NUM = (long)[[event.allTouches anyObject] phase];
    Byte sendbyte1 = [sender tag];
//    static int oneMoved= 0;
    
    if (isLockMode) {
        if(NUM == UITouchPhaseEnded){
            
//            if (oneMoved==1) {
//                oneMoved =0;
//                return;
//            }
//            NSLog(@"%d 按键按了一下",sendbyte1);
            
            if ([sender isSelected]) {
                [sender setTitleColor:[self getBlueColor] forState:UIControlStateNormal];
                [sender setTintColor:[UIColor whiteColor]];
                [sender setBackgroundColor:[UIColor whiteColor]];
                [sender setSelected:NO];
                //uncheck
                
                sendBytes3[1]=sendbyte1;
                sendBytes3[2]=((UISlider *)_fifteenSliders[sendbyte1]).value;
                [self sendBytes3:sendBytes3];
                
            }else{
                [sender setBackgroundColor:[self getBlueColor]];
                [sender setTintColor:[self getBlueColor]];
                [sender setSelected:YES];
                //check
                
                sendBytes3[1]=sendbyte1;
                sendBytes3[2]=(Byte)0x7F;
                [self sendBytes3:sendBytes3];
                
            }
        }
//        else if (UITouchPhaseMoved==NUM){
//            oneMoved = 1;
//        }
        
    }else{
        if (NUM == UITouchPhaseBegan) {//刚开始按下按键
            NSLog(@"按下 %d",sendbyte1);
            [sender setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
            [sender setBackgroundColor:[self getBlueColor]];
            [sender setTintColor:[self getBlueColor]];
            [sender setSelected:YES];
            
            
            sendBytes3[1]=sendbyte1;
            sendBytes3[2]=(Byte)0x7F;
            [self sendBytes3:sendBytes3];
            
        }else{
            NSLog(@"离开%d",sendbyte1);
            [sender setTitleColor:[self getBlueColor] forState:UIControlStateNormal];
            [sender setTintColor:[UIColor whiteColor]];
            [sender setBackgroundColor:[UIColor whiteColor]];
            [sender setSelected:NO];
            
            //uncheck
            
            sendBytes3[1]=sendbyte1;
            sendBytes3[2]=((UISlider *)_fifteenSliders[sendbyte1]).value;
            [self sendBytes3:sendBytes3];
            
        }
    }
}

//- (IBAction)tapGesture:(UITapGestureRecognizer *)sender {
//    
//    //轻击后要做的事情
////    NSLog(@"点击了 第%d页 第%d行",whichPage,GValue);
//    
//    self.tooltipManager = [[JDFSequentialTooltipManager alloc] initWithHostView:self.view];
//    
//    [self.tooltipManager addTooltipWithTargetView:_whichG hostView:self.view tooltipText:@"This is a tooltip with the backrop enabled. Tap anywhere to advance to the next tooltip." arrowDirection:JDFTooltipViewArrowDirectionLeft width:_topview.bounds.size.width];
//
//    self.tooltipManager.showsBackdropView = YES;
//    [self.tooltipManager showNextTooltip];
//}

-(UIColor*)getBlueColor
{
    CGColorSpaceRef colorSpaceRef = CGColorSpaceCreateDeviceRGB();
    CGColorRef color = CGColorCreate(colorSpaceRef, (CGFloat[]){0,0.5,1,1});
    return [UIColor colorWithCGColor:color];
}


//以下两个方法配合delegate使用，可以使能是否响应长按事件
//-(BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceivePress:(UIPress *)press
//{
//    return YES;
//}
//
//-(BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch
//{
//    return isLockMode;//longpress生效
//}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

-(void)sendBytes2:(Byte*)data
{
    if ([Socket isConnected]) {
        [Socket writeData:[NSData dataWithBytes:data length:2] withTimeout:-1 tag:100];
    }
}

-(void)sendBytes3:(Byte*)data
{
    if ([Socket isConnected]) {
        [Socket writeData:[NSData dataWithBytes:data length:3] withTimeout:-1 tag:100];
    }
}


-(void)updateAllseekbar
{
    int i=0;
    NSString* KEY;
    NSInteger value;
    for (UISlider *slider in _fifteenSliders)
    {
        KEY = [NSString stringWithFormat:@"%d%02d",(whichPage*10+GValue),i+1];
        value = [_defaults integerForKey:KEY];
        [slider setValue:value];
        [[_SliderValueText objectAtIndex:i] setText:[NSString stringWithFormat:@"%d",value]];
        i++;
    }
}


-(void)updateAllbtnText
{
    
    NSDictionary* dict = [_defaults objectForKey:DEFAULTSETTING];
    if (dict == nil) {
        _dict = [[NSMutableDictionary alloc] initWithCapacity:450];
    }else{
        _dict = [NSMutableDictionary dictionaryWithDictionary:dict];
    }
    
    int i=0;
    NSString* KEY;
    NSString* VALUE_TITLE;
    for (UIButton *btn in _fifteenButtons) {
        
        KEY = [NSString stringWithFormat:@"%d%02d",(whichPage*10+GValue),i+1];
        VALUE_TITLE = [_dict objectForKey:KEY];
        if (VALUE_TITLE!=nil) {
            [btn setTitle:VALUE_TITLE forState:UIControlStateNormal];
        }
        i++;
    }
}


-(void)SliderResetZero
{
    int i=0;
    NSString* KEY;
    for (UISlider *slider in _fifteenSliders)
    {
        KEY = [NSString stringWithFormat:@"%d%02d",(whichPage*10+GValue),i+1];
        [_defaults setInteger:0 forKey:KEY];
        [slider setValue:0 animated:YES];
        [[_SliderValueText objectAtIndex:i] setText:@"0"];
        
        sendBytes3[1]=(Byte)i;
        sendBytes3[2]=0;
        [self sendBytes3:sendBytes3];
        
        i++;
    }
}

-(void)allSliderResetZero
{
    
    [self SliderResetZero];
    NSString* KEY;
    for(int i=1;i<=3;i++)
    {
        for (int j = 0; j < 10; j++) {
            
            if(i==whichPage && j == (GValue-1))
                continue;
            
            for (int k = 1; k <= 15; k++) {
                KEY = [NSString stringWithFormat:@"%d%02d",(i*10+j),k];
                [_defaults setInteger:0 forKey:KEY];
            }
        }
    }
}


- (void)socket:(GCDAsyncSocket *)sock didWriteDataWithTag:(long)tag
{
}

- (void)socket:(GCDAsyncSocket *)sock didReadData:(NSData *)data withTag:(long)tag
{
}

- (void)socketDidDisconnect:(GCDAsyncSocket *)sock withError:(NSError *)err
{
    NSLog(@"失败信息: %@, 错误码：%d",[[err userInfo] objectForKey:@"NSLocalizedDescription"],[err code]);
    
    ZBHAlertViewController *alertView = [[ZBHAlertViewController alloc] initWithTitle:@"重新连接？" message:@"网络发生了错误！" viewController:self];
    
    RIButtonItem *cancelItem = [RIButtonItem itemWithLabel:@"取消" action:^{
        
    }];
    [alertView addButton:cancelItem type:RIButtonItemType_Cancel];
    
    RIButtonItem *okItem = [RIButtonItem itemWithLabel:@"确定" action:^{
        [self dismissViewControllerAnimated:YES completion:nil];
        
    }];
    [alertView addButton:okItem type:RIButtonItemType_Destructive];
    [alertView show];
}


-(int)charNumber:(NSString*)aString subString:(NSString**)outString outLength:(int)len{
    
    int strlength = 0;
    char* p = (char*)[aString cStringUsingEncoding:NSUTF8StringEncoding];
    const char* a = p;
    for (int i=0 ; i<[aString lengthOfBytesUsingEncoding:NSUTF8StringEncoding] ;i++) {
        if (*p) {
            if(*p == '\xe4' || *p == '\xe5' || *p == '\xe6' || *p == '\xe7' || *p == '\xe8' || *p == '\xe9')
            {
                strlength--;
            }
            p++;
            strlength++;
        }
        else {
            p++;
        }
    }
    
    if (strlength>len) {
        *outString = [[NSString alloc] initWithBytes:a length:len encoding:NSUTF8StringEncoding];
    }else{
        *outString = [NSString stringWithCString:a encoding:NSUTF8StringEncoding];
    }
    return strlength;
}

@end
