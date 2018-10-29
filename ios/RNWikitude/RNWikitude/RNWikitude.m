//
//  RNWikitude.m
//  RNWikitude
//
//  Created by Brave Digital Machine 7 on 2017/09/05.
//  Copyright © 2017 Brave Digital. All rights reserved.
//

#import "RNWikitude.h"
#import <React/RCTBridge.h>
#import <React/RCTUIManager.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTLog.h>
#import <React/RCTUtils.h>

// import RCTLog
#if __has_include(<React/RCTLog.h>)
#import <React/RCTLog.h>
#elif __has_include("RCTLog.h")
#import "RCTLog.h"
#else
#import "React/RCTLog.h"   // Required when used as a Pod in a Swift project
#endif


#define ERROR_PICKER_CANNOT_RUN_CAMERA_ON_SIMULATOR_KEY @"E_PICKER_CANNOT_RUN_CAMERA_ON_SIMULATOR"
#define ERROR_PICKER_CANNOT_RUN_CAMERA_ON_SIMULATOR_MSG @"Cannot run camera on simulator"

#define ERROR_PICKER_NO_CAMERA_PERMISSION_KEY @"E_PICKER_NO_CAMERA_PERMISSION"
#define ERROR_PICKER_NO_CAMERA_PERMISSION_MSG @"User did not grant camera permission."

#define ERROR_PICKER_UNAUTHORIZED_KEY @"E_PERMISSION_MISSING"
#define ERROR_PICKER_UNAUTHORIZED_MSG @"Cannot access images. Please allow access if you want to be able to select images."


@implementation RNWikitude
{
    BOOL _hasListeners;
}
- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()


- (instancetype)init
{
    if (self = [super init]) {
        
    }
    
    return self;
}

- (void) setConfiguration:(NSDictionary *)options
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject {
    
    self.reject = reject;
}

- (UIViewController*) getRootVC {
    UIViewController *root = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
    while (root.presentedViewController != nil) {
        root = root.presentedViewController;
    }
    
    return root;
}

- (void)checkCameraPermissions:(void(^)(BOOL granted))callback
{
    AVAuthorizationStatus status = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
    if (status == AVAuthorizationStatusAuthorized) {
        callback(YES);
        return;
    } else if (status == AVAuthorizationStatusNotDetermined){
        [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted) {
            callback(granted);
            return;
        }];
    } else {
        callback(NO);
    }
}

RCT_EXPORT_METHOD(stopAR)
{
    [self.arViewController stopWikitudeSDKRendering];
    [[self getRootVC] dismissViewControllerAnimated:YES completion:nil];
}

RCT_EXPORT_METHOD(startAR:(NSString *)url hasGeolocation:(BOOL *)geo hasImageRecognition:(BOOL *)image hasInstantTracking:(BOOL *)instant wikitudeSDKKey:(NSString *)sdkkey)
{
    
    
#if TARGET_IPHONE_SIMULATOR
    self.reject(ERROR_PICKER_CANNOT_RUN_CAMERA_ON_SIMULATOR_KEY, ERROR_PICKER_CANNOT_RUN_CAMERA_ON_SIMULATOR_MSG, nil);
    return;
#else
    [self checkCameraPermissions:^(BOOL granted) {
        if (!granted) {
            self.reject(ERROR_PICKER_NO_CAMERA_PERMISSION_KEY, ERROR_PICKER_NO_CAMERA_PERMISSION_MSG, nil);
            return;
        }
        
        ARViewController *arView = [[ARViewController alloc] init];
        arView.url = url;
        arView.sdkkey = sdkkey;
        self.arViewController = arView;
        
        __weak RNWikitude *weakSelf = self;
        
        void (^onJSONSent)(NSDictionary *) = ^void(NSDictionary * jsonObject) {
            if (_hasListeners) {
                [weakSelf sendEventWithName:@"json-sent" body: jsonObject];
            }
        };
        
        arView.onJSONSentBlock = onJSONSent;
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [[self getRootVC] presentViewController:arView animated:YES completion:nil];
        });
    }];
#endif
}

RCT_REMAP_METHOD(jsonSent, jsonSentWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    self.arViewController.onJSONSentBlock = resolve;
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"json-sent"];
}

// Will be called when this module's first listener is added.
- (void)startObserving
{
    _hasListeners = YES;
}

// Will be called when this module's last listener is removed, or on dealloc.
- (void)stopObserving
{
    _hasListeners = NO;
}

@end
