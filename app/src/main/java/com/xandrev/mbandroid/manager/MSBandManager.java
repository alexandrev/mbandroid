package com.xandrev.mbandroid.manager;

import android.app.Activity;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.notifications.MessageFlags;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageLayout;
import com.xandrev.mbandroid.tiles.CommonTile;
import com.xandrev.mbandroid.tiles.notifications.NotificationTile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by alexa on 12/11/2015.
 */
public class MSBandManager {

    private static final String TAG = "MSBandManager";
    private Activity mainActivity = null;
    private BandClient client = null;
    private static MSBandManager instance;

    public MSBandManager(Activity activity) {
        mainActivity = activity;
        Log.d(TAG,mainActivity.getLocalClassName());
        try {
            connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BandException e) {
            e.printStackTrace();
        }
    }

    public static final MSBandManager getInstance(Activity activity){
        if(instance == null){
            instance = new MSBandManager(activity);
        }
        return instance;
    }

    public void sendMessage(CommonTile tile,String title,String message) {
        try {
            client.getNotificationManager().sendMessage(tile.getId(), title, message, new Date(), MessageFlags.SHOW_DIALOG);
        } catch (BandIOException e) {
            e.printStackTrace();
        }
    }

    public  boolean connect() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                return false;
            }
            client = BandClientManager.getInstance().create(mainActivity.getApplicationContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }
        return ConnectionState.CONNECTED == client.connect().await();
    }

    public void disconnect(){
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
    }

    private BandTile createBandTile(CommonTile tile){

        List<BandTile> tiles = null;
        try {
            tiles = client.getTileManager().getTiles().await();
            UUID id = tile.getId();
            for(BandTile internalTile : tiles){
                if(id.equals(internalTile.getTileId())){
                    return null;
                }
            }
            BandTile.Builder builder = new BandTile.Builder(tile.getId(), tile.getName(), tile.getIcon());
            builder.setTileSmallIcon(tile.getSmallIcon(), true);
            if(tile.getPageLayout() != null) {
                builder.setPageLayouts(tile.getPageLayout());
            }
            return builder.build();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BandException e) {
                e.printStackTrace();
            }
        return null;
    }

    public boolean clearPages(CommonTile tile){
        boolean out = false;
        Log.i(TAG, "Clearing pages from tile ...\n");
        if(tile != null) {
            try {
                out =  client.getTileManager().removePages(tile.getId()).await();
                out = client.getTileManager().setPages(tile.getId(),tile.getPage()).await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BandException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "Pages cleared: "+out);
        return out;
    }

    public BandTile getCommonTileFromUUID(UUID tileID) {
        if(tileID != null) {
            Log.i(TAG,"Searching tile from UUID: "+tileID);
            List<BandTile> tiles = null;
            try {
                tiles = client.getTileManager().getTiles().await();
                Log.i(TAG,"Tile List:"+tiles.size());
                for (BandTile tile : tiles) {
                    if (tileID.equals(tile.getTileId())) {
                        return tile;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BandException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean addTile(CommonTile tile) throws Exception {
        Log.i(TAG, "Tile is adding ...\n");
        BandTile msTile = createBandTile(tile);
        if(msTile != null) {
            if (client.getTileManager().addTile(mainActivity, msTile).await()) {
                client.getTileManager().setPages(msTile.getTileId(),tile.getPage());
                Log.i(TAG, "Tile is added.\n");
                return true;
            } else {
                Log.i(TAG, "Unable to add tile to the band.\n");
            }
        }
        Log.i(TAG, "Tile is not added.\n");
        return false;
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public Activity getMainActivity(){
        return mainActivity;
    }

    public boolean addPage(CommonTile msTile) {

            Log.i(TAG,"Removing the page from the tile");
            BandTile uid = getCommonTileFromUUID(msTile.getId());
            if(uid != null){
                Log.i(TAG,"Size:  the page to the tile" + uid.getPageLayouts().size());
            }
//            boolean result = client.getTileManager().removePages(msTile.getPage().getPageId()).await();
    //        Log.i(TAG,"Removed the page from the tile: "+result);
            Log.i(TAG,"Adding the page to the tile");
  //          result = client.getTileManager().setPages(msTile.getId(),msTile.getPage()).await();
      //      Log.i(TAG,"Added the page to the tile: "+result);
            return true;
    }
}
