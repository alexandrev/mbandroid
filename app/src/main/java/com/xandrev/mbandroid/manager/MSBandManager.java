package com.xandrev.mbandroid.manager;

import android.app.Activity;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandConnectionCallback;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.notifications.MessageFlags;
import com.microsoft.band.tiles.BandTile;
import com.xandrev.mbandroid.gui.mBandroid;
import com.xandrev.mbandroid.services.LogViewer;
import com.xandrev.mbandroid.tiles.CommonTile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by alexa on 12/11/2015.
 */
public class MSBandManager {

    private static final String TAG = "MSBandManager";
    private mBandroid mainActivity = null;
    private BandClient client = null;
    private static MSBandManager instance;
    private LogViewer logViewer;

    public MSBandManager(mBandroid activity) {
        logViewer = LogViewer.getInstance(activity);
        mainActivity = activity;
        Log.d(TAG,"Activity: " + mainActivity.getLocalClassName());
        try {
            connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BandException e) {
            e.printStackTrace();
        }
    }

    public static final MSBandManager getInstance(mBandroid activity){
        //if(instance == null){
            instance = new MSBandManager(activity);
        //}
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
        Log.d(TAG,"Client devices detected: "+client);
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            Log.d(TAG,"Devices: "+devices);
            Log.d(TAG,"Band devices detected: "+devices.length);
            if (devices == null) {
                return false;
            }
            client = BandClientManager.getInstance().create(mainActivity.getApplicationContext(), devices[0]);
            Log.d(TAG,"Client devices detected: "+client);
            Log.d(TAG,"Client devices detected: "+client.getConnectionState().toString());
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }
        client.registerConnectionCallback(new BandConnectionCallback() {
            @Override
            public void onStateChanged(ConnectionState connectionState) {
                Log.i(TAG,"Connection status change to: "+connectionState.toString());
                logViewer.addMessage("Band connected: "+ connectionState.toString());
                Log.i(TAG,"2 Connection status change to: "+connectionState.toString());
                mainActivity.updateBandStatus(connectionState.toString());
                Log.i(TAG,"3 Connection status change to: "+connectionState.toString());
            }
        });
        boolean out = ConnectionState.CONNECTED == client.connect().await();
        Log.d(TAG,"Output of the connect method: "+out);

        return out;
    }

    public void disconnect(){
        Log.d(TAG,"Starting the disconnecting operation");
        if (client != null) {
            try {
                Log.d(TAG,"(client != null) Starting the disconnecting operation");
                client.disconnect().await();
                Log.d(TAG,"(client != null) Disconnecting operation completed");
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
        Log.d(TAG,"Disconnecting operation completed");
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
            Log.i(TAG,"Adding the page to the tile");
            return true;
    }

    public String getVersion() {

        Log.d(TAG,"Recovering the firmware version of the MS Band");
        String out = "";
        if(isConnected()) {
            Log.d(TAG,"Retrieving band version as it is connected");
            try {
                String firmware = client.getFirmwareVersion().await();
                String hardware = client.getHardwareVersion().await();
                out += hardware + " / " + firmware;
            } catch (BandException ex) {

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG,"Version value: "+out);
        return out;
    }
}
