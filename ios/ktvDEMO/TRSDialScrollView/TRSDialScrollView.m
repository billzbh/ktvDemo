/***************************************************
 *  ____              ___   ____                   *
 * |  _ \ __ _ _ __  ( _ ) / ___|  ___ __ _ _ __   *
 * | |_) / _` | '_ \ / _ \/\___ \ / __/ _` | '_ \  *
 * |  __/ (_| | | | | (_>  <___) | (_| (_| | | | | *
 * |_|   \__,_|_| |_|\___/\/____/ \___\__,_|_| |_| *
 *                                                 *
 ***************************************************/

#import "TRSDialView.h"
#import "TRSDialScrollView.h"

#define HeightRange (scrollView.contentSize.height-self.bounds.size.height)



@interface TRSDialScrollView () <UIScrollViewDelegate> {
    
    NSInteger _currentValue;
    double startTime;
    float startOffsetY;
}

@property (assign, nonatomic) NSInteger min;
@property (assign, nonatomic) NSInteger max;
@property (strong, nonatomic) UIScrollView *scrollView;
@property (strong, nonatomic) UIView *overlayView;
@property (strong, nonatomic) TRSDialView *dialView;

@end

@implementation TRSDialScrollView


- (void)commonInit
{
    _max = 0;
    _min = 0;
    
    float contentHeight = self.bounds.size.height;
    float contentWidth = self.bounds.size.width;
    
    _overlayView = [[UIView alloc] initWithFrame:self.bounds];
    [_overlayView setUserInteractionEnabled:NO];
    
    //设置背景颜色
    CAGradientLayer *_gradientLayer = [CAGradientLayer layer];  // 设置渐变效果
    
    _gradientLayer.bounds = [_overlayView bounds];
    _gradientLayer.borderWidth = 0;
    
    _gradientLayer.frame = [_overlayView bounds];
    _gradientLayer.colors = [NSArray arrayWithObjects:
                             (id)[[UIColor lightGrayColor] CGColor],
                             (id)[[UIColor clearColor] CGColor],
                             (id)[[UIColor clearColor] CGColor], (id)[[UIColor lightGrayColor] CGColor],nil];
    _gradientLayer.startPoint = CGPointMake(0.0, 0.0);
    _gradientLayer.endPoint = CGPointMake(0.0, 1.0);
    _gradientLayer.locations = @[@0.0, @0.49,@0.51, @1.0];
    
    [[_overlayView layer] insertSublayer:_gradientLayer atIndex:0];
    
    // Set the default frame size
    // Don't worry, we will be changing this later
    
    
//    _dialView = [[TRSDialView alloc] initWithFrame:CGRectMake(0, 0, self.bounds.size.width, contentHeight)];
    _dialView = [[TRSDialView alloc] initWithFrame:CGRectMake(0, 0, contentWidth, contentHeight)];
    
    // Don't let the container handle User Interaction
    [_dialView setUserInteractionEnabled:NO];
    
    _scrollView = [[UIScrollView alloc] initWithFrame:self.bounds];
    
    // Disable scroll bars
    [_scrollView setShowsHorizontalScrollIndicator:NO];
    [_scrollView setShowsVerticalScrollIndicator:NO];
    [_scrollView setPagingEnabled:NO];
    [_scrollView setClipsToBounds:YES];
    
    _scrollView.contentSize = CGSizeMake(contentWidth, _dialView.frame.size.height);
    
//    _scrollView.contentSize = CGSizeMake(_dialView.frame.size.width, contentHeight);
    
    
    // Setup the ScrollView
    [_scrollView setBounces:YES];
    [_scrollView setBouncesZoom:NO];
    _scrollView.delegate = self;
    
    [_scrollView addSubview:_dialView];
    [self addSubview:_scrollView];
    [self addSubview:_overlayView];
    
    // Clips the Dial View to the bounds of this view
    self.clipsToBounds = YES;
    
    
    [[TRSDialScrollView appearance] setMinorTicksPerMajorTick:10];
    [[TRSDialScrollView appearance] setMinorTickDistance:16];
    
    
    //设置背景图片
    [[TRSDialScrollView appearance] setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"Background"]]];
    //    [[TRSDialScrollView appearance] setOverlayColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"DialShadding"]]];
    
    [[TRSDialScrollView appearance] setLabelStrokeColor:[UIColor colorWithRed:0.400 green:0.525 blue:0.643 alpha:1.000]];
    [[TRSDialScrollView appearance] setLabelStrokeWidth:0.1f];
    [[TRSDialScrollView appearance] setLabelFillColor:[UIColor colorWithRed:0.098 green:0.220 blue:0.396 alpha:1.000]];
    
    [[TRSDialScrollView appearance] setLabelFont:[UIFont fontWithName:@"Avenir" size:20]];
    
    [[TRSDialScrollView appearance] setMinorTickColor:[UIColor colorWithRed:0.800 green:0.553 blue:0.318 alpha:1.000]];
    [[TRSDialScrollView appearance] setMinorTickLength:15.0];
    [[TRSDialScrollView appearance] setMinorTickWidth:1.0];
    
    [[TRSDialScrollView appearance] setMajorTickColor:[UIColor colorWithRed:0.098 green:0.220 blue:0.396 alpha:1.000]];
    [[TRSDialScrollView appearance] setMajorTickLength:33.0];
    [[TRSDialScrollView appearance] setMajorTickWidth:2.0];
    
    [[TRSDialScrollView appearance] setShadowColor:[UIColor colorWithRed:0.593 green:0.619 blue:0.643 alpha:1.000]];
    [[TRSDialScrollView appearance] setShadowOffset:CGSizeMake(0, 1)];
    [[TRSDialScrollView appearance] setShadowBlur:0.9f];
    [self setDialRangeFrom:0 to:1000];
    [self setCurrentValue:10];

}

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        [self commonInit];
    }
    
    return self;
}


- (instancetype)initWithCoder:(NSCoder *)coder
{
    self = [super initWithCoder:coder];
    
    if (self) {

        [self commonInit];
    }
    
    return self;
}

#pragma mark - Methods

- (void)setDialRangeFrom:(NSInteger)from to:(NSInteger)to {
    
    self.min = from;
    self.max = to;
    
    // Update the dial view
    [self.dialView setDialRangeFrom:from to:to];

//    self.scrollView.contentSize = CGSizeMake(self.dialView.frame.size.width, self.bounds.size.height);
    self.scrollView.contentSize = CGSizeMake(self.bounds.size.width,self.dialView.frame.size.height);
}

- (CGPoint)scrollToOffset:(CGPoint)starting {
    
    // Initialize the end point with the starting position
    CGPoint ending = starting;
    
    // Calculate the ending offset
//    ending.x = roundf(starting.x / self.minorTickDistance) * self.minorTickDistance;
    ending.y = roundf(starting.y / self.minorTickDistance) * self.minorTickDistance;
//    NSLog(@"starting=%f, ending=%f", starting.y, ending.y);
    
    return ending;
}

#pragma mark - UIScrollViewDelegate

- (BOOL)respondsToSelector:(SEL)aSelector
{
    if ([super respondsToSelector:aSelector])
        return YES;
    
    if ([self.delegate respondsToSelector:aSelector])
        return YES;
    
    return NO;
}

- (id)forwardingTargetForSelector:(SEL)aSelector
{
    if ([self.delegate respondsToSelector:aSelector])
        return self.delegate;

    // Always call parent object for default
    return [super forwardingTargetForSelector:aSelector];
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
//    startTime = [NSDate timeIntervalSinceReferenceDate];
//    startOffsetY = scrollView.contentOffset.y;
    
    if ([self.delegate respondsToSelector:@selector(scrollViewWillBeginDragging:)])
        [self.delegate scrollViewWillBeginDragging:scrollView];
}

- (void)scrollViewWillEndDragging:(UIScrollView *)scrollView
                     withVelocity:(CGPoint)velocity
              targetContentOffset:(inout CGPoint *)targetContentOffset {
    
    // Make sure that we scroll to the nearest tick mark on the dial.
    *targetContentOffset = [self scrollToOffset:(*targetContentOffset)];
    
    if ([self.delegate respondsToSelector:@selector(scrollViewWillEndDragging:withVelocity:targetContentOffset:)])
        
        [self.delegate scrollViewWillEndDragging:scrollView
                                    withVelocity:velocity
                             targetContentOffset:targetContentOffset];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    float  offsetY = scrollView.contentOffset.y;
    
    if (offsetY<=10) {
        
        
        [UIView animateWithDuration:0.5f animations:^{
            [self setCurrentValue:990];
        }];

    }else if(offsetY>= HeightRange-10)
    {
        
        [UIView animateWithDuration:0.5f animations:^{
            [self setCurrentValue:10];
        }];
    }
    
    if ([self.delegate respondsToSelector:@selector(scrollViewDidScroll:)])
        [self.delegate scrollViewDidScroll:scrollView];
    
    
    static NSInteger last_value = 10;
    
    // Calculate the value based on the content offset
    NSInteger value = self.currentValue;
    
    if (value != last_value) {
        if(value - last_value < 0)
        {
//            NSLog(@"new =%d,old = %d",value,last_value);
            if(value == 10 &&  1000 -last_value < 50 )
            {
//                NSLog(@"正在向上滑888");
                [self.SlipDirectiondelegate slipToUP:YES tag:(int)self.tag];
                
            }else{
//                NSLog(@"正在向下滑");
                [self.SlipDirectiondelegate slipToUP:NO tag:(int)self.tag];
            }
        }else{
            
//            NSLog(@"new =%d,old = %d",value,last_value);
            if(value == 990 && last_value < 50)
            {
//                NSLog(@"正在向下滑888");
                [self.SlipDirectiondelegate slipToUP:NO tag:(int)self.tag];
                
            }else{
//                NSLog(@"正在向上滑");
                [self.SlipDirectiondelegate slipToUP:YES tag:(int)self.tag];
            }
        }
    }
    
    last_value = value;
}

#pragma mark - Properties

- (void)setMinorTicksPerMajorTick:(NSInteger)minorTicksPerMajorTick
{
    self.dialView.minorTicksPerMajorTick = minorTicksPerMajorTick;
}

- (NSInteger)minorTicksPerMajorTick
{
    return self.dialView.minorTicksPerMajorTick;
}

- (void)setMinorTickDistance:(NSInteger)minorTickDistance
{
    self.dialView.minorTickDistance = minorTickDistance;
}

- (NSInteger)minorTickDistance
{
    return self.dialView.minorTickDistance;
}

- (void)setBackgroundColor:(UIColor *)backgroundColor
{
    self.dialView.backgroundColor = backgroundColor;
}

- (UIColor *)backgroundColor
{
    return self.dialView.backgroundColor;
}

- (void)setLabelStrokeColor:(UIColor *)labelStrokeColor
{
    self.dialView.labelStrokeColor = labelStrokeColor;
}

- (UIColor *)labelStrokeColor
{
    return self.dialView.labelStrokeColor;
}

- (void)setLabelFillColor:(UIColor *)labelFillColor
{
    self.dialView.labelFillColor = labelFillColor;
}

- (void)setLabelStrokeWidth:(CGFloat)labelStrokeWidth
{
    self.dialView.labelStrokeWidth = labelStrokeWidth;
}

- (CGFloat)labelStrokeWidth
{
    return self.dialView.labelStrokeWidth;
}

- (UIColor *)labelFillColor
{
    return self.dialView.labelFillColor;
}

- (void)setLabelFont:(UIFont *)labelFont
{
    self.dialView.labelFont = labelFont;
}

- (UIFont *)labelFont
{
    return self.dialView.labelFont;
}

- (void)setMinorTickColor:(UIColor *)minorTickColor
{
    self.dialView.minorTickColor = minorTickColor;
}

- (UIColor *)minorTickColor
{
    return self.dialView.minorTickColor;
}

- (void)setMinorTickLength:(CGFloat)minorTickLength
{
    self.dialView.minorTickLength = minorTickLength;
}

- (CGFloat)minorTickLength
{
    return self.dialView.minorTickLength;
}

- (void)setMinorTickWidth:(CGFloat)minorTickWidth
{
    self.dialView.minorTickWidth = minorTickWidth;
}

- (CGFloat)minorTickWidth
{
    return self.dialView.minorTickWidth;
}

- (void)setMajorTickColor:(UIColor *)majorTickColor
{
    self.dialView.majorTickColor = majorTickColor;
}

- (UIColor *)majorTickColor
{
    return self.dialView.majorTickColor;
}

- (void)setMajorTickLength:(CGFloat)majorTickLength
{
    self.dialView.majorTickLength = majorTickLength;
}

- (CGFloat)majorTickLength
{
    return self.dialView.majorTickLength;
}

- (void)setMajorTickWidth:(CGFloat)majorTickWidth
{
    self.dialView.majorTickWidth = majorTickWidth;
}

- (CGFloat)majorTickWidth
{
    return self.dialView.majorTickWidth;
}

- (void)setShadowColor:(UIColor *)shadowColor
{
    self.dialView.shadowColor = shadowColor;
}

- (UIColor *)shadowColor
{
    return self.dialView.shadowColor;
}

- (void)setShadowOffset:(CGSize)shadowOffset
{
    self.dialView.shadowOffset = shadowOffset;
}

- (CGSize)shadowOffset
{
    return self.dialView.shadowOffset;
}

- (void)setShadowBlur:(CGFloat)shadowBlur
{
    self.dialView.shadowBlur = shadowBlur;
}

- (CGFloat)shadowBlur
{
    return self.dialView.shadowBlur;
}

- (void)setOverlayColor:(UIColor *)overlayColor
{
    self.overlayView.backgroundColor = overlayColor;
}

- (UIColor *)overlayColor
{
    return self.overlayColor;
}

- (void)setCurrentValue:(NSInteger)newValue {
    
    // Check to make sure the value is within the available range
    if ((newValue < _min) || (newValue > _max))
        _currentValue = _min;
    
    else
        _currentValue = newValue;
    
    // Update the content offset based on new value
    CGPoint offset = self.scrollView.contentOffset;

    offset.y = (newValue - self.dialView.minimum) * self.dialView.minorTickDistance;
//        offset.x = (newValue - self.dialView.minimum) * self.dialView.minorTickDistance;
    
    self.scrollView.contentOffset = offset;
}

- (NSInteger)currentValue
{
//    return roundf(self.scrollView.contentOffset.x / self.dialView.minorTickDistance) + self.dialView.minimum;
        return roundf(self.scrollView.contentOffset.y / self.dialView.minorTickDistance) + self.dialView.minimum;
}

-(UIView *)getScrollView
{
    return _scrollView;
}
-(UIView *)getOverlayView;
{
    return _overlayView;
}

@end
