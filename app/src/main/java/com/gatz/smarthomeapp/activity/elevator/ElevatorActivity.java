package com.gatz.smarthomeapp.activity.elevator;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dnake.smart.WakeTask;
import com.dnake.v700.Elevator;
import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.dnake.v700.sys;
import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.base.BaseActivity;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ElevatorActivity extends BaseActivity {
    private ImageButton elevatorOneDown, elevatorOneUp;
    private TextView elevatorData[] = null;
    private int direct[] = new int[3];
    private long aTs = 0;
    private RelativeLayout backLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elevator);
        initView();
        Elevator.query();


    }

    @Override
    public void onStart() {
        super.onStart();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ElevatorActivity.this.finish();
            }
        }, 1000 * 180);
    }

    private void initView() {
        elevatorData = new TextView[2];
        elevatorOneDown = (ImageButton) findViewById(R.id.elevator_one_down);
        elevatorOneUp = (ImageButton) findViewById(R.id.elevator_one_up);
        elevatorData[0] = (TextView) findViewById(R.id.tv_elevator_floor_one);
        elevatorData[1] = (TextView) findViewById(R.id.tv_elevator_floor_two);
        elevatorOneDown.setOnClickListener(twoElevatorListener);
        elevatorOneUp.setOnClickListener(twoElevatorListener);
        backLayout = (RelativeLayout) findViewById(R.id.elevator_top_layout);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ElevatorActivity.this.finish();
            }
        });

    }

    private View.OnClickListener twoElevatorListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.elevator_one_down:
                    Elevator.appoint(0, 2);
                    Elevator.appoint(1, 2);
                    break;
                case R.id.elevator_one_up:
                    Elevator.appoint(0, 1);
                    Elevator.appoint(1, 1);
                    break;
                default:
                    break;
            }
        }
    };

    private long ts = 0;

    @Override
    public void onTimer() {
        super.onTimer();
        for (int i = 0; i < elevatorData.length; i++) {
            if (sys.elev[i].refresh) {
                sys.elev[i].refresh = false;

                String s = sys.elev[i].display;
                if (sys.elev[i].sign == -1) {
                    s = "-" + sys.elev[i].display;
                }
                elevatorData[i].setText(s);

                if (direct[i] != sys.elev[i].direct) {
                    direct[i] = sys.elev[i].direct;
                    if (direct[i] == 0x03 && sys.elev[i].sign != -1 && Integer.parseInt(sys.elev[i].display) == sys.talk.floor) {
                        playSound("/dnake/bin/prompt/elv_arrival.wav");
                        Toast.makeText(this, R.string.elevator_prompt_arrival, Toast.LENGTH_LONG).show();
//                        if (direct[i] == 1) {
//                            liftDownIb.setBackgroundResource(R.drawable.homepage_elevator_btn_left_s);
//                        } else if (direct[i] == 2) {
//                            liftDownIb.setBackgroundResource(R.drawable.homepage_elevator_btn_right_s);
//                        }

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                ElevatorActivity.this.finish();
                            }
                        }, 1000 * 10);


                        aTs = 0;
                    }
                }
            }
        }

        if (sys.query.sip.url != null && Math.abs(System.currentTimeMillis() - ts) >= 2 * 1000) {
            ts = System.currentTimeMillis();

            dxml p = new dxml();
            dmsg req = new dmsg();
            p.setText("/params/to", sys.query.sip.url);
            p.setText("/params/data/params/event_url", "/elev/join");
            req.to("/talk/sip/sendto", p.toString());

            if (Math.abs(System.currentTimeMillis() - aTs) < 2 * 60 * 1000)
                WakeTask.acquire();
        }
    }

    private MediaPlayer playSound(String url) {
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(url);
            mp.prepare();
            mp.setLooping(false);
            mp.start();

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer p) {
                    p.stop();
                    p.release();
                }
            });
            return mp;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mp != null) {
            mp.stop();
            mp.release();
        }
        return null;
    }


}
