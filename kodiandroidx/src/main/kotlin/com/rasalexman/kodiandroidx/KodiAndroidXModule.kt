package com.rasalexman.kodiandroidx

import android.accounts.AccountManager
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.DownloadManager
import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.SearchManager
import android.app.UiModeManager
import android.app.WallpaperManager
import android.app.admin.DevicePolicyManager
import android.app.job.JobScheduler
import android.app.usage.NetworkStatsManager
import android.app.usage.UsageStatsManager
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothManager
import android.content.ClipboardManager
import android.content.Context
import android.content.RestrictionsManager
import android.content.pm.LauncherApps
import android.content.pm.ShortcutManager
import android.hardware.ConsumerIrManager
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.hardware.input.InputManager
import android.hardware.usb.UsbManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.media.midi.MidiManager
import android.media.projection.MediaProjectionManager
import android.media.session.MediaSessionManager
import android.media.tv.TvInputManager
import android.net.ConnectivityManager
import android.net.nsd.NsdManager
import android.net.wifi.p2p.WifiP2pManager
import android.nfc.NfcManager
import android.os.BatteryManager
import android.os.Build
import android.os.DropBoxManager
import android.os.Environment
import android.os.HardwarePropertiesManager
import android.os.PowerManager
import android.os.UserManager
import android.os.health.SystemHealthManager
import android.os.storage.StorageManager
import android.print.PrintManager
import android.telecom.TelecomManager
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.CaptioningManager
import android.view.inputmethod.InputMethodManager
import android.view.textservice.TextServicesManager
import com.rasalexman.kodi.core.bind
import com.rasalexman.kodi.core.import
import com.rasalexman.kodi.core.instance
import com.rasalexman.kodi.core.kodiModule
import com.rasalexman.kodi.core.provider
import com.rasalexman.kodi.core.with
import java.io.File

const val CACHE_DIR = "cache"
const val EXT_CACHE_DIR = "externalCache"
const val FILES_CACHE_DIR = "files"
const val OBB_CACHE_DIR = "obb"

val kodiDirAndroidXModule by lazy {
    kodiModule {
        bind<File>(tag = CACHE_DIR) with provider { instance<Context>().cacheDir }
        bind<File>(tag = FILES_CACHE_DIR) with provider { instance<Context>().filesDir }
        bind<File>(tag = OBB_CACHE_DIR) with provider { instance<Context>().obbDir }

        if (hasExternalSDCard()) {
            instance<Context>().externalCacheDir?.let {
                bind<File>(tag = EXT_CACHE_DIR) with provider { it }
            }
        }
    }
}

val kodiAndroidXManagers by lazy {
    kodiModule {
        bind<AccessibilityManager>() with provider { instance<Context>().getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager }
        bind<AccountManager>() with provider { instance<Context>().getSystemService(Context.ACCOUNT_SERVICE) as AccountManager }
        bind<ActivityManager>() with provider { instance<Context>().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }
        bind<AlarmManager>() with provider { instance<Context>().getSystemService(Context.ALARM_SERVICE) as AlarmManager }
        bind<AudioManager>() with provider { instance<Context>().getSystemService(Context.AUDIO_SERVICE) as AudioManager }
        bind<ClipboardManager>() with provider { instance<Context>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
        bind<ConnectivityManager>() with provider { instance<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
        bind<DevicePolicyManager>() with provider { instance<Context>().getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager }
        bind<DownloadManager>() with provider { instance<Context>().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }
        bind<DropBoxManager>() with provider { instance<Context>().getSystemService(Context.DROPBOX_SERVICE) as DropBoxManager }
        bind<InputMethodManager>() with provider { instance<Context>().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
        bind<KeyguardManager>() with provider { instance<Context>().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager }
        bind<LayoutInflater>() with provider { instance<Context>().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater }
        bind<LocationManager>() with provider { instance<Context>().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
        bind<NfcManager>() with provider { instance<Context>().getSystemService(Context.NFC_SERVICE) as NfcManager }
        bind<NotificationManager>() with provider { instance<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
        bind<PowerManager>() with provider { instance<Context>().getSystemService(Context.POWER_SERVICE) as PowerManager }
        bind<SearchManager>() with provider { instance<Context>().getSystemService(Context.SEARCH_SERVICE) as SearchManager }
        bind<SensorManager>() with provider { instance<Context>().getSystemService(Context.SENSOR_SERVICE) as SensorManager }
        bind<StorageManager>() with provider { instance<Context>().getSystemService(Context.STORAGE_SERVICE) as StorageManager }
        bind<TelephonyManager>() with provider { instance<Context>().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager }
        bind<TextServicesManager>() with provider { instance<Context>().getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE) as TextServicesManager }
        bind<UiModeManager>() with provider { instance<Context>().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager }
        bind<UsbManager>() with provider { instance<Context>().getSystemService(Context.USB_SERVICE) as UsbManager }
        //bind<Vibrator>() with provider { instance<Context>().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }
        bind<WallpaperManager>() with provider { instance<Context>().getSystemService(Context.WALLPAPER_SERVICE) as WallpaperManager }
        bind<WifiP2pManager>() with provider { instance<Context>().getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager }
        //bind<WifiManager>() with provider { instance<Context>().getSystemService(Context.WIFI_SERVICE) as WifiManager }
        bind<WindowManager>() with provider { instance<Context>().getSystemService(Context.WINDOW_SERVICE) as WindowManager }

        bind<InputManager>() with provider { instance<Context>().getSystemService(Context.INPUT_SERVICE) as InputManager }
        bind<MediaRouter>() with provider { instance<Context>().getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter }
        bind<NsdManager>() with provider { instance<Context>().getSystemService(Context.NSD_SERVICE) as NsdManager }

        bind<DisplayManager>() with provider { instance<Context>().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager }
        bind<UserManager>() with provider { instance<Context>().getSystemService(Context.USER_SERVICE) as UserManager }

        bind<BluetoothManager>() with provider { instance<Context>().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }

        bind<AppOpsManager>() with provider { instance<Context>().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager }
        bind<CaptioningManager>() with provider { instance<Context>().getSystemService(Context.CAPTIONING_SERVICE) as CaptioningManager }
        bind<ConsumerIrManager>() with provider { instance<Context>().getSystemService(Context.CONSUMER_IR_SERVICE) as ConsumerIrManager }
        bind<PrintManager>() with provider { instance<Context>().getSystemService(Context.PRINT_SERVICE) as PrintManager }
    }
}

val kodiAndroidXManagers21 by lazy {
    kodiModule {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bind<AppWidgetManager>() with provider { instance<Context>().getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager }
            bind<BatteryManager>() with provider { instance<Context>().getSystemService(Context.BATTERY_SERVICE) as BatteryManager }
            bind<CameraManager>() with provider { instance<Context>().getSystemService(Context.CAMERA_SERVICE) as CameraManager }
            bind<JobScheduler>() with provider { instance<Context>().getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler }
            bind<LauncherApps>() with provider { instance<Context>().getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps }
            bind<MediaProjectionManager>() with provider { instance<Context>().getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager }
            bind<MediaSessionManager>() with provider { instance<Context>().getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager }
            bind<RestrictionsManager>() with provider { instance<Context>().getSystemService(Context.RESTRICTIONS_SERVICE) as RestrictionsManager }
            bind<TelecomManager>() with provider { instance<Context>().getSystemService(Context.TELECOM_SERVICE) as TelecomManager }
            bind<TvInputManager>() with provider { instance<Context>().getSystemService(Context.TV_INPUT_SERVICE) as TvInputManager }
        }
    }
}

val kodiAndroidXManagers22 by lazy {
    kodiModule {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            bind<SubscriptionManager>() with provider { instance<Context>().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager }
            bind<UsageStatsManager>() with provider { instance<Context>().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager }
        }
    }
}

val kodiAndroidXManagers23 by lazy {
    kodiModule {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bind<CarrierConfigManager>() with provider { instance<Context>().getSystemService(Context.CARRIER_CONFIG_SERVICE) as CarrierConfigManager }
            bind<MidiManager>() with provider { instance<Context>().getSystemService(Context.MIDI_SERVICE) as MidiManager }
            bind<NetworkStatsManager>() with provider { instance<Context>().getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager }
        }
    }
}

val kodiAndroidXManagers24 by lazy {
    kodiModule {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bind<HardwarePropertiesManager>() with provider { instance<Context>().getSystemService(Context.HARDWARE_PROPERTIES_SERVICE) as HardwarePropertiesManager }
            bind<SystemHealthManager>() with provider { instance<Context>().getSystemService(Context.SYSTEM_HEALTH_SERVICE) as SystemHealthManager }
        }
    }
}

val kodiAndroidXManagers25 by lazy {
    kodiModule {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            bind<ShortcutManager>() with provider { instance<Context>().getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager }
        }
    }
}

val kodiAndroidXModule by lazy {
    kodiModule {
        import(kodiDirAndroidXModule)
        import(kodiAndroidXManagers)
        import(kodiAndroidXManagers21)
        import(kodiAndroidXManagers22)
        import(kodiAndroidXManagers23)
        import(kodiAndroidXManagers24)
        import(kodiAndroidXManagers25)
    }
}

private fun hasExternalSDCard(): Boolean {
    return try {
        val state = Environment.getExternalStorageState()
        (Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state)
    } catch (e: Throwable) {
        false
    }
}