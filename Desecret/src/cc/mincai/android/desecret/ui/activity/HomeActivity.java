package cc.mincai.android.desecret.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cc.mincai.android.desecret.R;
import cc.mincai.android.desecret.model.*;
import cc.mincai.android.desecret.service.DesecretService;
import cc.mincai.android.desecret.util.Action1;

import java.util.LinkedList;

public class HomeActivity extends Activity {
    private DesecretService desecretService;
    private ServiceConnection masterServiceConnection;

    private LinkedList<ActivityEventWrapper> listItems;
    private ArrayAdapter<ActivityEventWrapper> listAdapter;

    private ListView listViewContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        this.listViewContacts = (ListView) findViewById(R.id.listViewContacts);

        this.listViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ActivityEventWrapper selectedActivityEventWrapper = (ActivityEventWrapper) listViewContacts.getItemAtPosition(position);

                if(selectedActivityEventWrapper.getActivityEvent() instanceof LocationChangedEvent) {
                    Intent launchingIntent = new Intent(HomeActivity.this, MapActivity.class);
                    launchingIntent.putExtra("location", ((LocationChangedEvent) selectedActivityEventWrapper.getActivityEvent()).getNewLocation());
                    startActivity(launchingIntent);
                }
                else if(selectedActivityEventWrapper.getActivityEvent() instanceof SmsReceivedEvent) {
                    //TODO
                }
                else if(selectedActivityEventWrapper.getActivityEvent() instanceof SmsSentEvent) {
                    //TODO
                }
                else if(selectedActivityEventWrapper.getActivityEvent() instanceof PhoneCallReceivedEvent) {
                    //TODO
                }
                else if(selectedActivityEventWrapper.getActivityEvent() instanceof PhoneCallSentEvent) {
                    //TODO
                }
                else {
                    //TODO
                }
            }
        });

        this.listItems = new LinkedList<ActivityEventWrapper>();

        this.listAdapter = new ActivityEventWrapperArrayAdapter(this, android.R.layout.simple_list_item_1, this.listItems);

        this.listViewContacts.setAdapter(this.listAdapter);

        this.masterServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder binder) {
                desecretService = ((DesecretService.MyBinder) binder).getService();

                desecretService.getDesecretClient().addTargetActivityEventOccurredEventListener(new Action1<DesecretClient.TargetActivityEventOccurredEvent>() {
                    @Override
                    public void apply(final DesecretClient.TargetActivityEventOccurredEvent event) {
                        onTargetActivityEventOccurred(event.getFrom(), event.getEvent());
                    }
                });
            }

            public void onServiceDisconnected(ComponentName className) {
                desecretService = null;
            }
        };

        this.bindService(
                new Intent(this, DesecretService.class),
                this.masterServiceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    private void onTargetActivityEventOccurred(final String from, final ActivityEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listItems.addFirst(new ActivityEventWrapper(from, event));
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(this.masterServiceConnection);
    }

    public class ActivityEventWrapper {
        private String from;
        private ActivityEvent activityEvent;

        public ActivityEventWrapper(String from, ActivityEvent activityEvent) {
            this.from = from;
            this.activityEvent = activityEvent;
        }

        public String getFrom() {
            return from;
        }

        public ActivityEvent getActivityEvent() {
            return activityEvent;
        }


        @Override
        public String toString() {
            return String.format("ActivityEventWrapper{from='%s\', activityEvent=%s}", from, activityEvent);
        }
    }

    private class ActivityEventWrapperArrayAdapter extends ArrayAdapter<ActivityEventWrapper> {
        private LinkedList<ActivityEventWrapper> items;

        public ActivityEventWrapperArrayAdapter(Context context, int textViewResourceId, LinkedList<ActivityEventWrapper> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.simple_list_item_1, null);
            }
            ActivityEventWrapper o = items.get(position);
            if (o != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                tt.setText(o.getFrom() + ": " + o.getActivityEvent());
                bt.setText("" + o.getActivityEvent().getTimeCreated());
            }
            return v;
        }
    }
}
