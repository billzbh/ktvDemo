//
//  ViewController.m
//  ktvDEMO
//
//  Created by zbh on 16/4/5.
//  Copyright © 2016年 hxsmart. All rights reserved.
//

#import "ViewController.h"
#import "GCDAsyncSocket.h"
#include <netinet/tcp.h>
#include <netinet/in.h>

#define DEFAULTSETTING @"LIUJIEWEN"
#define DEFAULTSETTING_BAK @"LIUJIEWEN_BAK"
#define PORT 8080
#define DEFALUT_IP @"192.168.11.254"

@interface ViewController (){
    BOOL isHideSetting;
    NSString *IP;
    GCDAsyncSocket* Socket;
}

@property (strong,nonatomic) NSMutableDictionary* dict;

@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *TaskRunFlag;
@property (weak, nonatomic) IBOutlet UILabel *ConnectInfo;
@property (weak, nonatomic) IBOutlet UIButton *TryAgain;
@property (weak, nonatomic) IBOutlet UIButton *Setting;
@property (strong,nonatomic) NSUserDefaults* defaults;
@property (weak, nonatomic) IBOutlet UIButton *clearFlagBtn;

@property (weak, nonatomic) IBOutlet UIButton *offLine;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    isHideSetting = YES;//是否显示【设置】按钮
    self.defaults = [NSUserDefaults standardUserDefaults];
    _dict = [[NSMutableDictionary alloc] init];

    [_TryAgain.layer setMasksToBounds:YES];//设置按钮的圆角半径不会被遮挡
    [_TryAgain.layer setCornerRadius:4];
    [_TryAgain.layer setBorderWidth:1];//设置边界的宽度
    
    [_clearFlagBtn.layer setMasksToBounds:YES];//设置按钮的圆角半径不会被遮挡
    [_clearFlagBtn.layer setCornerRadius:4];
    [_clearFlagBtn.layer setBorderWidth:1];//设置边界的宽度
    
    [_offLine.layer setMasksToBounds:YES];//设置按钮的圆角半径不会被遮挡
    [_offLine.layer setCornerRadius:4];
    [_offLine.layer setBorderWidth:1];//设置边界的宽度
    
    //设置按钮的边界颜色
    CGColorSpaceRef colorSpaceRef = CGColorSpaceCreateDeviceRGB();
    CGColorRef color = CGColorCreate(colorSpaceRef, (CGFloat[]){0,0.5,1,1});
    [_TryAgain.layer setBorderColor:color];
    [_clearFlagBtn.layer setBorderColor:color];
    [_offLine.layer setBorderColor:color];
    
    // Do any additional setup after loading the view, typically from a nib.
    Socket = [GCDAsyncSocket sharedController];
}

-(void)viewWillAppear:(BOOL)animated
{
    //1. 读取设置，连接网络
    IP = [self.defaults objectForKey:@"IP"];
    if(IP==nil || [IP isEqualToString:@""])
        IP=DEFALUT_IP;
    NSInteger channel = [self.defaults integerForKey:@"CHANNEL"];
    if(channel<0 || channel>9)
        [self.defaults setInteger:0 forKey:@"CHANNEL"];
    
    [Socket setDelegate:self];
    [self connectToServer];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)settingAction:(UIButton *)sender {
    [_ConnectInfo setText:@"断开连接..."];
    [Socket setDelegate:nil];
    [Socket disconnect];
}

- (IBAction)Tryagain:(id)sender {
    [_TryAgain setHidden:YES];
    [_clearFlagBtn setHidden:YES];
    [_offLine setHidden:YES];
    [_TaskRunFlag startAnimating];
    [self connectToServer];
}

- (IBAction)clearAciton:(UIButton *)sender {
    
    NSString *KEY;
    NSString *Value;
    for(int i=1;i<=3;i++)
    {
        for (int j = 1; j <= 10; j++) {
            for (int k = 1; k <= 15; k++) {
                Value = [NSString stringWithFormat:@"\n%02d\n",k];
                KEY = [NSString stringWithFormat:@"%d%02d",(i*10+j),k];
                //清除状态
                [_dict setValue:Value forKey:KEY];
            }
        }
    }
    [_defaults setObject:_dict forKey:DEFAULTSETTING];
    [sender setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
}

- (IBAction)restoreAction:(UILongPressGestureRecognizer *)sender {
    //恢复最近一次的标识
    if([(UILongPressGestureRecognizer*)sender state] == UIGestureRecognizerStateBegan){
//        NSLog(@"=====LongPress====");
        NSString *KEY;
        NSString *Value;
        NSString *BakValue;
        NSDictionary *dictBak = [_defaults objectForKey:DEFAULTSETTING_BAK];
        for(int i=1;i<=3;i++)
        {
            for (int j = 1; j <= 10; j++) {
                for (int k = 1; k <= 15; k++) {
                    Value = [NSString stringWithFormat:@"\n%02d\n",k];
                    KEY = [NSString stringWithFormat:@"%d%02d",(i*10+j),k];
                    BakValue = [dictBak objectForKey:KEY];
                    if(dictBak==nil || BakValue == nil)
                    {
                        //NSLog(@"此%@没有对应的值，设置默认值%@",KEY,Value);
                        [_dict setValue:Value forKey:KEY];
                    }
                    //先读备份，再恢复
                    [_dict setValue:BakValue forKey:KEY];
                }
            }
        }
        
        [_defaults setObject:_dict forKey:DEFAULTSETTING];
        [_clearFlagBtn setTitleColor:[self getBlueColor] forState:UIControlStateNormal];
    }
    
}

- (IBAction)go2WorkView:(UIButton *)sender {
    //转到工作界面
    UIStoryboard *board = [UIStoryboard storyboardWithName: @"Main" bundle: nil];
    UIViewController* childController = [board instantiateViewControllerWithIdentifier: @"workingViewController"];
    [self presentViewController:childController animated:YES completion:nil];
}


-(UIColor*)getBlueColor
{
    CGColorSpaceRef colorSpaceRef = CGColorSpaceCreateDeviceRGB();
    CGColorRef color = CGColorCreate(colorSpaceRef, (CGFloat[]){0,0.5,1,1});
    return [UIColor colorWithCGColor:color];
}


-(void)connectToServer
{
    if ([Socket isConnected]) {
        [_ConnectInfo setText:@"已经连接,请不要重复连接"];
        [_TaskRunFlag stopAnimating];
        return;
    }
    
    NSLog(@"connecting to %@ %u",IP,PORT);
    [_ConnectInfo setText:@"连接中..."];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        NSError *error = nil;
        [Socket connectToHost:IP onPort:PORT withTimeout:5 error:&error];
    });
}


- (void)socket:(GCDAsyncSocket *)sock didConnectToHost:(NSString *)host port:(uint16_t)port
{
    NSLog(@"CONNECT %@[%d] ok",host,port);
    [_ConnectInfo setText:@"连接成功！"];
    [_TryAgain setHidden:NO];
    [_clearFlagBtn setHidden:NO];
    
    if(!isHideSetting)
        [_Setting setHidden:NO];
    [_TaskRunFlag stopAnimating];
    
    
    //设置 Tcp_NoDelay
    [Socket performBlock:^{
        int fd = [Socket socketFD];
        int on = 1;
        if (setsockopt(fd, IPPROTO_TCP, TCP_NODELAY, (char*)&on, sizeof(on)) == -1) {
            /* handle error */
            NSLog(@"设置TCP_NoDelay失败！");
        }
    }];
    
    //转到工作界面
    UIStoryboard *board = [UIStoryboard storyboardWithName: @"Main" bundle: nil];
    UIViewController* childController = [board instantiateViewControllerWithIdentifier: @"workingViewController"];
    [self presentViewController:childController animated:YES completion:nil];
}

- (void)socketDidDisconnect:(GCDAsyncSocket *)sock withError:(NSError *)err
{
    NSLog(@"连接错误：%@",err);
    [_ConnectInfo setText:@"连接失败！换个姿势，我们可以再来一次！"];
    [_TryAgain setHidden:NO];
    [_clearFlagBtn setHidden:NO];
    [_offLine setHidden:NO];
    if(!isHideSetting)
        [_Setting setHidden:NO];
    [_TaskRunFlag stopAnimating];
}

@end
