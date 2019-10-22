package com.github.rkhusainov.simpledraw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private Button mCurveButton;
    private Button mLineButton;
    private Button mBoxButton;
    private Button mPolyButton;
    private Button mRedColorButton;
    private Button mBlackColorButton;
    private Button mGreenColorButton;
    private Button mBlueColorButton;
    private Button mYellowColorButton;
    private Button mOrangeColorButton;
    private Button mMagentaColorButton;
    private Button mPinkColorButton;
    private ImageButton mClearImageButton;
    private DrawView mDrawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawView = findViewById(R.id.draw_view);

        buttonInit();
    }

    private void buttonInit() {
        mCurveButton = findViewById(R.id.btn_curve);
        mLineButton = findViewById(R.id.btn_line);
        mBoxButton = findViewById(R.id.btn_box);
        mPolyButton = findViewById(R.id.btn_poly);

        mRedColorButton = findViewById(R.id.btn_color_red);
        mBlackColorButton = findViewById(R.id.btn_color_black);
        mGreenColorButton = findViewById(R.id.btn_color_green);
        mBlueColorButton = findViewById(R.id.btn_color_blue);
        mYellowColorButton = findViewById(R.id.btn_color_yellow);
        mOrangeColorButton = findViewById(R.id.btn_color_orange);
        mMagentaColorButton = findViewById(R.id.btn_color_magenta);
        mPinkColorButton = findViewById(R.id.btn_color_pink);

        mClearImageButton = findViewById(R.id.btn_clear);

        mCurveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setDrawType(DrawType.CURVE);
            }
        });

        mLineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setDrawType(DrawType.LINE);
            }
        });

        mBoxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setDrawType(DrawType.BOX);
            }
        });

        mPolyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setDrawType(DrawType.POLY);
            }
        });

        mBlackColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setPaintColor(getResources().getColor(R.color.colorBlack));
            }
        });

        mRedColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setPaintColor(getResources().getColor(R.color.colorRed));
            }
        });

        mGreenColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setPaintColor(getResources().getColor(R.color.colorGreen));
            }
        });

        mClearImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.clear();
            }
        });

        mBlueColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setPaintColor(getResources().getColor(R.color.colorBlue));
            }
        });
        mYellowColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setPaintColor(getResources().getColor(R.color.colorYellow));
            }
        });
        mOrangeColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setPaintColor(getResources().getColor(R.color.colorOrange));
            }
        });
        mMagentaColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setPaintColor(getResources().getColor(R.color.colorMagenta));
            }
        });
        mPinkColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setPaintColor(getResources().getColor(R.color.colorPink));
            }
        });

    }
}
