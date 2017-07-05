#import "AudioplayPlugin.h"
#import <AudioToolbox/AudioToolbox.h>

@implementation AudioplayPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"audioplay"
            binaryMessenger:[registrar messenger]];
  AudioplayPlugin* instance = [[AudioplayPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result{
 if([@"play" isEqualToString:call.method]) {
    NSString *path;
    if([@"courier" isEqualToString:call.arguments[@"name"]]){
    path  = [[NSBundle mainBundle] pathForResource:@"courier" ofType:@"mp3"];
    } else {
    path  = [[NSBundle mainBundle] pathForResource:@"notification" ofType:@"mp3"];
    }

    NSURL *pathURL = [NSURL fileURLWithPath : path];

    SystemSoundID audioEffect;
    AudioServicesCreateSystemSoundID((__bridge CFURLRef) pathURL, &audioEffect);
    AudioServicesPlaySystemSound(audioEffect);

    // call the following function when the sound is no longer used
    // (must be done AFTER the sound is done playing)
    //AudioServicesDisposeSystemSoundID(audioEffect);
    result(@"ok");
 } else {
   result(FlutterMethodNotImplemented);
 }
}

@end
