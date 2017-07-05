package com.yourcompany.audioplay;

        import android.app.Activity;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.os.Handler;
        import android.util.Log;
        import io.flutter.app.FlutterActivity;
        import io.flutter.plugin.common.MethodChannel;
        import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
        import io.flutter.plugin.common.MethodChannel.Result;
        import io.flutter.plugin.common.MethodCall;
        import io.flutter.plugin.common.PluginRegistry.Registrar;

        import java.io.IOException;
        import java.util.HashMap;
        import java.util.Timer;
        import java.util.TimerTask;

/**
 * AudioplayerPlugin
 */
public class AudioplayPlugin implements MethodCallHandler {
  private final MethodChannel channel;
  private Activity activity;

  Timer timer;
  String lastUrl;

  final Handler handler = new Handler();

  MediaPlayer mediaPlayer;

  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "audioplay");
    channel.setMethodCallHandler(new AudioplayPlugin(registrar.activity(), channel));
  }

  private AudioplayPlugin(Activity activity, MethodChannel channel) {
    this.activity = activity;
    this.channel = channel;
    this.channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(MethodCall call, MethodChannel.Result response) {
    if (call.method.equals("play")) {
      String type = ((HashMap) call.arguments()).get("name").toString();
      System.out.println(type);
      Boolean resPlay = play(!type.equals("courier") ? "https://firebasestorage.googleapis.com/v0/b/firebase-fdc.appspot.com/o/sounds%2Fnotification.mp3?alt=media&token=a3d2ea1e-e26d-425b-98df-714d4ffbd0d9" : "https://firebasestorage.googleapis.com/v0/b/firebase-fdc.appspot.com/o/sounds%2Fcourier.mp3?alt=media&token=eaf0ca12-9119-469f-9baa-bcdb81fc7026");
      response.success(1);
    } else if (call.method.equals("pause")) {
      pause();
      response.success(1);
    } else if (call.method.equals("stop")) {
      stop();
      response.success(1);
    } else {
      response.notImplemented();
    }
  }

  private void stop() {
    if(timer != null) {
      timer.cancel();
    }
  }

  private void pause() {
    mediaPlayer.pause();
    handler.removeCallbacks(sendData);
  }

  private Boolean play(String url) {

    if(mediaPlayer != null) {
      if (mediaPlayer.isPlaying()) {
        return false;
      }
    }

    if (!url.equals(lastUrl)) {
      mediaPlayer = new MediaPlayer();
      mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

      try {
        mediaPlayer.setDataSource(url);
      } catch (IOException e) {
        e.printStackTrace();
        Log.d("AUDIO", "invalid DataSource");
      }

      try {
        mediaPlayer.prepare();
      } catch (IOException e) {
        Log.d("AUDIO", "media prepare ERROR");
        e.printStackTrace();
      }
    }
    lastUrl = url;
    Log.d("URL", url);
    mediaPlayer.start();
    // if (url.equals("https://firebasestorage.googleapis.com/v0/b/firebase-fdc.appspot.com/o/sounds%2Fnotification.mp3?alt=media&token=a3d2ea1e-e26d-425b-98df-714d4ffbd0d9")) {
    //     timer = new Timer();
    //     TimerTask timerTask = new TimerTask() {
    //         @Override
    //         public void run() {
    //             Log.d("AUDIO", "running");
    //             mediaPlayer.start();
    //         }
    //     };
    //     timer.scheduleAtFixedRate(timerTask, 15000, 15000);
    // }

    return true;
  }

  private final Runnable sendData = new Runnable() {
    public void run() {
      try {
        if (!mediaPlayer.isPlaying()) {
          handler.removeCallbacks(sendData);
        }
        int time = mediaPlayer.getCurrentPosition();
        channel.invokeMethod("audio.onCurrentPosition", time);

        handler.postDelayed(this, 200);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  };
}
