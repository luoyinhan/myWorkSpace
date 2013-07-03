package com.coship.ott.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ProgramIntroActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_intro);
        Intent intent=getIntent();
        String poroIntro=intent.getStringExtra("programDesc");
        TextView programIntro=(TextView) this.findViewById(R.id.programIntro);
        programIntro.setText(poroIntro);
        ImageView programIntroExit=(ImageView) this.findViewById(R.id.programIntroExit);
        programIntroExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    }

}
