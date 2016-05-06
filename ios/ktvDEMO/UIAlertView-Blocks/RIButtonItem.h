//
//  RIButtonItem.h
//  Shibui
//
//  Created by Jiva DeVoe on 1/12/11.
//  Copyright 2011 Random Ideas, LLC. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RIButtonItem : NSObject
{
    NSString *label;
    void (^action)();
    void (^actionText)(NSString *PlainText);
}
@property (retain, nonatomic) NSString *label;
@property (copy, nonatomic) void (^action)();
@property (copy, nonatomic) void (^actionText)(NSString *PlainText);
+(id)item;
+(id)itemWithLabel:(NSString *)inLabel;
+(id)itemWithLabel:(NSString *)inLabel action:(void(^)(void))action;
@end

