//
//  SettingViewController.m
//  ktvDEMO
//
//  Created by zbh on 16/4/5.
//  Copyright © 2016年 hxsmart. All rights reserved.
//

#import "SettingViewController.h"
#define PORT 8080
#define DEFAULTSETTING @"LIUJIEWEN"
#define DEFAULTSETTING_BAK @"LIUJIEWEN_BAK"

@interface SettingViewController (){
    BOOL isDone;
}
@property (strong,nonatomic) NSString *IP;
@property NSInteger CHANNEL;
@property (strong,nonatomic) NSUserDefaults* defaults;
@property (strong,nonatomic) NSMutableDictionary* dict;

@property (weak, nonatomic) IBOutlet UITextField *editIP;
@property (weak, nonatomic) IBOutlet UILabel *chanel;
@property (weak, nonatomic) IBOutlet UIButton *clearButton;

@property (weak, nonatomic) IBOutlet UIStepper *Stepper;
@end

@implementation SettingViewController
- (IBAction)Chanel:(id)sender {
    
    [_chanel setText:[NSString stringWithFormat:@"%d",(int)[(UIStepper*)sender value]]];
}

- (IBAction)clearFlags:(UIButton*)sender {
    
    NSString *KEY;
    NSString *Value;
    for(int i=1;i<=3;i++)
    {
        for (int j = 0; j < 10; j++) {
            for (int k = 1; k <= 15; k++) {
                Value = [NSString stringWithFormat:@"\n%02d\n",k];
                KEY = [NSString stringWithFormat:@"%d%02d",(i*10+j),k];
                //清除状态
                [_dict setValue:Value forKey:KEY];
            }
        }
    }
    [_defaults setObject:_dict forKey:DEFAULTSETTING];
    [_defaults synchronize];
    [sender setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Do any additional setup after loading the view.
    isDone = NO;
//    NSLog(@"=====viewDidLoad====");
    //读取配置
    _defaults = [NSUserDefaults standardUserDefaults];
    _IP = [_defaults objectForKey:@"IP"];
    _CHANNEL = [_defaults integerForKey:@"CHANNEL"];
    _dict = [[NSMutableDictionary alloc] init];
    
    if (_IP) {
        [_editIP setText:_IP];
    }
    if (_CHANNEL) {
        [_chanel setText:[NSString stringWithFormat:@"%d",(int)_CHANNEL]];
        [_Stepper setValue:_CHANNEL];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

-(void)viewDidDisappear:(BOOL)animated
{
//    NSLog(@"=====viewDidDisappear====");
    //保存配置
    [_defaults setInteger:[[_chanel text] intValue] forKey:@"CHANNEL"];
    [_defaults setObject:[_editIP text] forKey:@"IP"];
    
}
- (IBAction)LongPress:(id)sender {
    
    //恢复最近一次的标识
    if([(UILongPressGestureRecognizer*)sender state] == UIGestureRecognizerStateBegan){
//        NSLog(@"=====LongPress====");
        NSString *KEY;
        NSString *Value;
        NSString *BakValue;
        NSDictionary *dictBak = [_defaults objectForKey:DEFAULTSETTING_BAK];
        for(int i=1;i<=3;i++)
        {
            for (int j = 0; j < 10; j++) {
                for (int k = 1; k <= 15; k++) {
                    Value = [NSString stringWithFormat:@"\n%02d\n",k];
                    KEY = [NSString stringWithFormat:@"%d%02d",(i*10+j),k];
                    
                    //读备份
                    BakValue = [dictBak objectForKey:KEY];
                    if(dictBak==nil || BakValue == nil)
                    {
//                        NSLog(@"此%@没有对应的值，设置默认值%@",KEY,Value);
                        [_dict setValue:Value forKey:KEY];
                    }
                    //再恢复
                    [_dict setValue:BakValue forKey:KEY];
                }
            }
        }
        
        [_defaults setObject:_dict forKey:DEFAULTSETTING];
        [_clearButton setTitleColor:[self getBlueColor] forState:UIControlStateNormal];
    }
}

- (IBAction)Done:(id)sender {
    isDone = YES;
    [_defaults setObject:[_editIP text] forKey:@"IP"];
}

- (IBAction)EndEdit:(id)sender {
    
    if(isDone)
    {
        isDone = NO;
        return;
    }
    [_defaults setObject:[_editIP text] forKey:@"IP"];
}
-(UIColor*)getBlueColor
{
    CGColorSpaceRef colorSpaceRef = CGColorSpaceCreateDeviceRGB();
    CGColorRef color = CGColorCreate(colorSpaceRef, (CGFloat[]){0,0.5,1,1});
    return [UIColor colorWithCGColor:color];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
