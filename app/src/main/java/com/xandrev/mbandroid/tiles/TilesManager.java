package com.xandrev.mbandroid.tiles;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.band.BandException;
import com.microsoft.band.tiles.TileButtonEvent;
import com.microsoft.band.tiles.TileEvent;
import com.xandrev.mbandroid.gui.mBandroid;
import com.xandrev.mbandroid.manager.LogViewer;
import com.xandrev.mbandroid.manager.MSBandManager;
import com.xandrev.mbandroid.notifications.BandStatusService;
import com.xandrev.mbandroid.settings.base.GeneralSettings;
import com.xandrev.mbandroid.settings.notifications.NotificationSettings;
import com.xandrev.mbandroid.tiles.mail.MailTile;
import com.xandrev.mbandroid.tiles.notifications.NotificationTile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * Created by alexa on 12/11/2015.
 */
public class TilesManager {

    private List<CommonTile> tiles;
    private List<UUID> internalTilesUUID;
    private MSBandManager bandManager;
    private static final String TAG = "TilesManager";
    private static TilesManager instance;
    private mBandroid activity;
    private Context context;
    private boolean isRunning = false;
    private GeneralSettings settings;
    private LogViewer logViewer;

    public static TilesManager getInstance(Context activity) {
        if(instance == null){
            instance = new TilesManager(activity);
        }
        return instance;
    }

    public void setActivity(mBandroid activity){
        this.activity = activity;
    }

    public TilesManager(Context ctx){
        logViewer = LogViewer.getInstance(ctx);
        tiles = new ArrayList<CommonTile>();
        internalTilesUUID = new ArrayList<>();
        this.context = ctx;
        settings = GeneralSettings.getInstance(ctx);
        initTiles();
    }

    private void initTiles() {
        MailTile.getInstance(this);
        NotificationTile.getInstance(this);
    }

    private boolean addTile(CommonTile tile) throws Exception {
        boolean out = false;
        if (tile != null && bandManager.getCommonTileFromUUID(tile.getId()) == null)  {
            out = bandManager.addTile(tile);
            tiles.add(tile);
            Log.i(TAG,"Tile added to the logical list: "+tile.getName());
            logViewer.addMessage("Tile added to the logical list: "+tile.getName());
        }
        return out;
    }

    public List<CommonTile> getTilesAffected(String pack) {
        List<CommonTile> tmpList = getActivatedTiles();
        List<CommonTile> target = new ArrayList<>();
        if(tmpList != null) {
            Log.i(TAG,"Activated Tiles: "+tmpList.size());
            for (CommonTile tmp : tmpList) {
                Log.i(TAG,"Tile Name: "+tmp.getName());
                if (tmp.isAffected(pack)) {
                    Log.i(TAG,"Tile Added");
                    target.add(tmp);
                }
            }
        }
        return target;
    }

    public Context getContext() {
        return context;
    }

    public MSBandManager getBand() {
        return bandManager;
    }

    public void start(mBandroid main){

        new appTask(main).execute();

    }

    public void stop() {
        bandManager.disconnect();
    }

    private class appTask extends AsyncTask<Void, Void, Void> {

        private mBandroid main;

        public appTask(mBandroid main){
            this.main = main;
        }

        public void setActivity(mBandroid act){
            this.main = act;
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                bandManager = MSBandManager.getInstance(main);
                if(!bandManager.isConnected()) {
                    bandManager.connect();
                }

                if(bandManager.isConnected()){
                    Intent serviceIntent = new Intent(context,BandStatusService.class);
                    EventBus.getDefault().postSticky(bandManager);
                    serviceIntent.putExtra("command", "start");
                    context.startService(serviceIntent);
                        Log.i(TAG, "Band is connected.\n");
                        List<CommonTile> tiles = getActivatedTiles();
                        if(tiles != null) {
                            Log.i(TAG,"Activated Tiles Size: "+tiles.size());
                            for (CommonTile tile : tiles) {
                                Log.i(TAG, "Common Tile Added: " + tile.getName());
                                addTile(tile);
                            }
                        }
                    } else {
                        Log.i(TAG, "Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                    }

            } catch (BandException e){
                String exceptionMessage = "";
                switch (e.getErrorType()) {
                    case DEVICE_ERROR:
                        exceptionMessage = "Please make sure bluetooth is on and the band is in range.\n";
                        break;
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    case BAND_FULL_ERROR:
                        exceptionMessage = "Band is full. Please use Microsoft Health to remove a tile.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                Log.i(TAG, exceptionMessage);
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

            return null;
        }
    }

    private List<CommonTile> getActivatedTiles() {
        List<CommonTile> out = new ArrayList<CommonTile>();
        List<String> tilesEnabled = settings.getEnabledTiles();
        if(tilesEnabled != null){
            for(int i=0;i<tilesEnabled.size();i++) {
                CommonTile tileObj = settings.getClassFromTile(this,tilesEnabled.get(i));
                if(tileObj != null){
                    out.add(tileObj);
                }
            }
        }
        return out;
    }


    public void activate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TileEvent.ACTION_TILE_OPENED);
        filter.addAction(TileEvent.ACTION_TILE_BUTTON_PRESSED);
        filter.addAction(TileEvent.ACTION_TILE_CLOSED);
        try {
            context.registerReceiver(messageReceiver, filter);

        }catch(IllegalArgumentException ex){

        }
    }

    public void deactivate(){
        try {
            context.unregisterReceiver(messageReceiver);
        }catch(IllegalArgumentException ex){

        }
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == TileEvent.ACTION_TILE_OPENED) {
                TileEvent tileOpenData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
                Log.i(TAG, "Tile open event received\n" + " " + "\n\n");
            } else if (intent.getAction() == TileEvent.ACTION_TILE_BUTTON_PRESSED) {
                TileButtonEvent buttonData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
                CommonTile tile = getCommonTileFromUUID(buttonData.getTileID());
                if(tile != null) {
                    tile.manageAction(intent);
                }
                Log.i(TAG, "Button event received\n" + buttonData.toString() + "\n\n");
            } else if (intent.getAction() == TileEvent.ACTION_TILE_CLOSED) {
                TileEvent tileCloseData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
                Log.i(TAG, "Tile close event received\n" + tileCloseData.toString() + "\n\n");
            } else if (intent.getAction() == TileEvent.TILE_EVENT_DATA) {
                Log.i(TAG, "Tile close event received\n" + intent.toString() + "\n\n");
            }
        }
    };

    public CommonTile getCommonTileFromUUID(UUID tileID) {
        if(tileID != null) {
            Log.i(TAG,"Searching tile from UUID: "+tileID);
            Log.i(TAG,"Tile List:"+tiles.size());
            for (CommonTile tile : tiles) {
                if (tileID.equals(tile.getId())) {
                    return tile;
                }
            }
        }
        return null;
    }
}
