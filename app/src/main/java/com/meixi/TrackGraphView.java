package com.meixi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

public class TrackGraphView extends View {
    private static int MOVE_HANDLE_LEFT;
    private static int MOVE_HANDLE_RIGHT;
    private static int MOVE_SCROLL;
    private static int SIDE_LEFT;
    private static int SIDE_RIGHT;
    TrackPosition m_HandleLeftTrackpos;
    TrackPosition m_HandleRightTrackpos;
    TrackGraphActivity m_Parent;
    float m_TouchMoveStartX;
    float m_TouchMoveStartY;
    long m_TouchTimerStart;
    Track m_TrackToDisplay;
    boolean m_bTouchHold;
    boolean m_bTouchMoveActive;
    double m_dHandleLeftPos;
    double m_dHandleRightPos;
    double m_dHandleWidthRatio;
    double m_dMaxY;
    double m_dMinY;
    double m_dScalePercent;
    double m_dScrollStartLeftHandle;
    double m_dScrollStartRightHandle;
    double m_dZoomCtrlYRatio;
    int m_iGrabWidth;
    int m_iHandleWidth;
    int m_iHorLinesCount;
    int m_iScaleHeight;
    int m_iTouchMoveMode;
    int m_iVertLinesCount;
    Rect m_rectBigGraph;
    Rect m_rectSmallGraph;
    Rect m_rectTimeGraph;

    private class TrackPosition {
        double m_dFraction;
        int m_iIndex;
        long m_lTime;

        private TrackPosition() {
            this.m_lTime = 0;
            this.m_iIndex = 0;
            this.m_dFraction = 0.0d;
        }
    }

    static {
        MOVE_HANDLE_LEFT = 1;
        MOVE_HANDLE_RIGHT = 2;
        MOVE_SCROLL = 3;
        SIDE_LEFT = 0;
        SIDE_RIGHT = 1;
    }

    public TrackGraphView(Context context, double dHandleLeft, double dHandleRight) {
        super(context);
        this.m_dHandleLeftPos = 0.0d;
        this.m_dHandleRightPos = 100.0d;
        this.m_bTouchHold = false;
        this.m_TouchMoveStartX = 25.0f;
        this.m_TouchMoveStartY = 25.0f;
        this.m_iTouchMoveMode = 0;
        this.m_iHandleWidth = 0;
        this.m_Parent = (TrackGraphActivity) context;
        this.m_TrackToDisplay = MMTrackerActivity.m_TrackToDisplayData;
        this.m_iHandleWidth = 0;
        this.m_dZoomCtrlYRatio = 20.0d;
        this.m_dHandleWidthRatio = 4.0d;
        this.m_dScalePercent = 8.0d;
        this.m_iGrabWidth = 3;
        this.m_dHandleLeftPos = dHandleLeft;
        this.m_dHandleRightPos = dHandleRight;
        this.m_HandleLeftTrackpos = CalcTimeFromPercent(dHandleLeft, SIDE_LEFT);
        this.m_HandleRightTrackpos = CalcTimeFromPercent(dHandleRight, SIDE_RIGHT);
    }

    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        Init(true);
    }

    public void Init(boolean bResetBitmap) {
        if (getHeight() > getWidth()) {
            this.m_iHandleWidth = (int) Math.round((this.m_dHandleWidthRatio / 100.0d) * ((double) getWidth()));
            this.m_iScaleHeight = (int) Math.round((this.m_dScalePercent / 100.0d) * ((double) getWidth()));
        } else {
            this.m_iHandleWidth = (int) Math.round((this.m_dHandleWidthRatio / 100.0d) * ((double) getHeight()));
            this.m_iScaleHeight = (int) Math.round((this.m_dScalePercent / 100.0d) * ((double) getHeight()));
        }
        this.m_rectBigGraph = new Rect(0, 0, getWidth(), (getHeight() - ((int) Math.round((this.m_dZoomCtrlYRatio / 100.0d) * ((double) getHeight())))) - this.m_iScaleHeight);
        this.m_rectSmallGraph = new Rect(this.m_iHandleWidth / 2, getHeight() - ((int) Math.round((this.m_dZoomCtrlYRatio / 100.0d) * ((double) getHeight()))), getWidth() - (this.m_iHandleWidth / 2), getHeight());
        this.m_rectTimeGraph = new Rect(0, this.m_rectBigGraph.bottom, getWidth(), this.m_rectSmallGraph.top);
        CalcLineCount();
        CalcMinMax(this.m_Parent.m_iGraphType);
        boolean bCreateBitmap = false;
        if (MMTrackerActivity.m_BitmapSmallGraph == null) {
            bCreateBitmap = true;
        } else if (getWidth() != MMTrackerActivity.m_BitmapSmallGraph.getWidth()) {
            bCreateBitmap = true;
        }
        if (bCreateBitmap) {
            MMTrackerActivity.m_BitmapSmallGraph = Bitmap.createBitmap(getWidth(), this.m_rectSmallGraph.height(), Config.ARGB_8888);
            MMTrackerActivity.m_canvasSmallGraph = new Canvas(MMTrackerActivity.m_BitmapSmallGraph);
        }
        MMTrackerActivity.m_canvasSmallGraph.drawARGB(255, 255, 255, 255);
        DrawGraph(MMTrackerActivity.m_canvasSmallGraph, this.m_rectSmallGraph, CalcTimeFromPercent(0.0d, SIDE_LEFT), CalcTimeFromPercent(100.0d, SIDE_RIGHT), this.m_Parent.m_iGraphType, 11141290);
    }

    private void touch_start(float x, float y) {
        this.m_TouchMoveStartX = x;
        this.m_TouchMoveStartY = y;
        this.m_bTouchMoveActive = false;
        this.m_bTouchHold = true;
        if (y <= ((float) ((int) Math.round(((double) getHeight()) - ((this.m_dZoomCtrlYRatio / 100.0d) * ((double) getHeight())))))) {
            this.m_iTouchMoveMode = MOVE_SCROLL;
            this.m_dScrollStartLeftHandle = this.m_dHandleLeftPos;
            this.m_dScrollStartRightHandle = this.m_dHandleRightPos;
        } else if (Math.abs((((double) x) - ((((double) (getWidth() - this.m_iHandleWidth)) * this.m_dHandleLeftPos) / 100.0d)) + ((double) (this.m_iHandleWidth / 2))) < ((double) (this.m_iHandleWidth * this.m_iGrabWidth))) {
            this.m_iTouchMoveMode = MOVE_HANDLE_LEFT;
        } else if (Math.abs((((double) x) - ((((double) (getWidth() - this.m_iHandleWidth)) * this.m_dHandleRightPos) / 100.0d)) + ((double) (this.m_iHandleWidth / 2))) < ((double) (this.m_iHandleWidth * this.m_iGrabWidth))) {
            this.m_iTouchMoveMode = MOVE_HANDLE_RIGHT;
        }
    }

    private void touch_move(float x, float y) {
        if (Math.abs(x - this.m_TouchMoveStartX) > 10.0f || Math.abs(y - this.m_TouchMoveStartY) > 10.0f || this.m_bTouchMoveActive) {
            this.m_bTouchMoveActive = true;
            this.m_bTouchHold = false;
            if (this.m_iTouchMoveMode == MOVE_HANDLE_LEFT) {
                this.m_dHandleLeftPos = (double) (((x - ((float) (this.m_iHandleWidth / 2))) / ((float) (getWidth() - this.m_iHandleWidth))) * 100.0f);
                if (this.m_dHandleLeftPos < 0.0d) {
                    this.m_dHandleLeftPos = 0.0d;
                }
                if (this.m_dHandleLeftPos > 100.0d) {
                    this.m_dHandleLeftPos = 100.0d;
                }
                if (this.m_dHandleLeftPos > this.m_dHandleRightPos - this.m_dHandleWidthRatio) {
                    this.m_dHandleLeftPos = this.m_dHandleRightPos - this.m_dHandleWidthRatio;
                }
                this.m_HandleLeftTrackpos = CalcTimeFromPercent(this.m_dHandleLeftPos, SIDE_LEFT);
                invalidate();
            }
            if (this.m_iTouchMoveMode == MOVE_HANDLE_RIGHT) {
                this.m_dHandleRightPos = (double) (((x - ((float) (this.m_iHandleWidth / 2))) / ((float) (getWidth() - this.m_iHandleWidth))) * 100.0f);
                if (this.m_dHandleRightPos < 0.0d) {
                    this.m_dHandleRightPos = 0.0d;
                }
                if (this.m_dHandleRightPos > 100.0d) {
                    this.m_dHandleRightPos = 100.0d;
                }
                if (this.m_dHandleRightPos < this.m_dHandleLeftPos + this.m_dHandleWidthRatio) {
                    this.m_dHandleRightPos = this.m_dHandleLeftPos + this.m_dHandleWidthRatio;
                }
                this.m_HandleRightTrackpos = CalcTimeFromPercent(this.m_dHandleRightPos, SIDE_RIGHT);
                invalidate();
            }
            if (this.m_iTouchMoveMode == MOVE_SCROLL) {
                double delta = ((double) (x - this.m_TouchMoveStartX)) * ((this.m_dHandleRightPos - this.m_dHandleLeftPos) / ((double) getWidth()));
                this.m_dHandleLeftPos = this.m_dScrollStartLeftHandle - delta;
                this.m_dHandleRightPos = this.m_dScrollStartRightHandle - delta;
                if (this.m_dHandleRightPos > 100.0d) {
                    this.m_dHandleLeftPos -= this.m_dHandleRightPos - 100.0d;
                    this.m_dHandleRightPos = 100.0d;
                }
                if (this.m_dHandleLeftPos < 0.0d) {
                    this.m_dHandleRightPos -= this.m_dHandleLeftPos;
                    this.m_dHandleLeftPos = 0.0d;
                }
                this.m_HandleLeftTrackpos = CalcTimeFromPercent(this.m_dHandleLeftPos, SIDE_LEFT);
                this.m_HandleRightTrackpos = CalcTimeFromPercent(this.m_dHandleRightPos, SIDE_RIGHT);
                invalidate();
            }
        }
    }

    private void touch_up() {
        this.m_bTouchHold = false;
        this.m_iTouchMoveMode = 0;
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case 0:
                touch_start(x, y);
                this.m_TouchTimerStart = System.currentTimeMillis();
                break;
            case 1:
                touch_up();
                if (!this.m_bTouchMoveActive) {
                    System.currentTimeMillis();
                    int i = MMTrackerActivity.TIME_TO_CONTEXT_MENU;
                }
                this.m_bTouchMoveActive = false;
                invalidate();
                break;
            case 2:
                touch_move(x, y);
                invalidate();
                break;
        }
        return true;
    }

    public void FullInvalidate() {
        this.m_HandleLeftTrackpos = CalcTimeFromPercent(this.m_dHandleLeftPos, SIDE_LEFT);
        this.m_HandleRightTrackpos = CalcTimeFromPercent(this.m_dHandleRightPos, SIDE_RIGHT);
        invalidate();
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(255, 255, 255, 255);
        DrawGraph(canvas, this.m_rectBigGraph, this.m_HandleLeftTrackpos, this.m_HandleRightTrackpos, this.m_Parent.m_iGraphType, 34952);
        DrawTimeLine(canvas, this.m_rectTimeGraph, this.m_HandleLeftTrackpos, this.m_HandleRightTrackpos);
        DrawGrid(canvas, this.m_rectBigGraph, this.m_dHandleLeftPos, this.m_dHandleRightPos, this.m_Parent.m_iGraphType);
        canvas.drawBitmap(MMTrackerActivity.m_BitmapSmallGraph, 0.0f, (float) (this.m_rectSmallGraph.top + 1), null);
        DrawOutlineAndHandles(canvas);
    }

    private void CalcMinMax(int iType) {
        ArrayList<Trackpoint> trackpoints = this.m_TrackToDisplay.trackpoints;
        double dTemp = 0.0d;
        this.m_dMinY = 999999.0d;
        this.m_dMaxY = -999999.0d;
        double dHeightFactor = Tools.m_dUnitHeightFactor[MMTrackerActivity.m_SettingsUnitsDistances];
        double dSpeedFactor = Tools.m_dUnitSpeedFactor[MMTrackerActivity.m_SettingsUnitsDistances];
        for (int i = 0; i < trackpoints.size(); i++) {
            if (iType == MMTrackerActivity.GRAPH_TYPE_SPEED) {
                dTemp = ((Trackpoint) trackpoints.get(i)).m_dVelocity * dSpeedFactor;
            }
            if (iType == MMTrackerActivity.GRAPH_TYPE_HEIGHT) {
                dTemp = ((Trackpoint) trackpoints.get(i)).m_dAltitude * dHeightFactor;
            }
            if (dTemp > this.m_dMaxY) {
                this.m_dMaxY = dTemp;
            }
            if (dTemp < this.m_dMinY) {
                this.m_dMinY = dTemp;
            }
        }
        this.m_dMaxY = this.m_dMinY + ((this.m_dMaxY - this.m_dMinY) * 1.1d);
        if (this.m_dMaxY == this.m_dMinY) {
            this.m_dMaxY = 2.0d * this.m_dMinY;
        }
        if (this.m_dMaxY == this.m_dMinY) {
            this.m_dMaxY = 1.0d;
        }
        this.m_dMinY = Math.floor(this.m_dMinY / ((double) this.m_iHorLinesCount)) * ((double) this.m_iHorLinesCount);
        this.m_dMaxY = (Math.ceil((this.m_dMaxY - this.m_dMinY) / ((double) this.m_iHorLinesCount)) * ((double) this.m_iHorLinesCount)) + this.m_dMinY;
    }

    private void CalcLineCount() {
        if (getHeight() < getWidth()) {
            this.m_iVertLinesCount = 8;
            this.m_iHorLinesCount = 4;
            return;
        }
        this.m_iVertLinesCount = 4;
        this.m_iHorLinesCount = 8;
    }

    private TrackPosition CalcTimeFromPercent(double dPercent, int iSide) {
        TrackPosition trackpos = new TrackPosition();
        if (this.m_TrackToDisplay != null && this.m_TrackToDisplay.trackpoints.size() > 1) {
            long lAbsoluteSeekTime = ((Trackpoint) this.m_TrackToDisplay.trackpoints.get(0)).m_lTime + Math.round((((double) (((Trackpoint) this.m_TrackToDisplay.trackpoints.get(this.m_TrackToDisplay.trackpoints.size() - 1)).m_lTime - ((Trackpoint) this.m_TrackToDisplay.trackpoints.get(0)).m_lTime)) / 100.0d) * dPercent);
            int i = 0;
            while (((Trackpoint) this.m_TrackToDisplay.trackpoints.get(i)).m_lTime < lAbsoluteSeekTime) {
                i++;
            }
            if (i > 0 && iSide == SIDE_LEFT && lAbsoluteSeekTime != ((Trackpoint) this.m_TrackToDisplay.trackpoints.get(i - 1)).m_lTime) {
                i--;
            }
            trackpos.m_iIndex = i;
            trackpos.m_lTime = lAbsoluteSeekTime;
            trackpos.m_dFraction = 0.0d;
            if (iSide == SIDE_RIGHT) {
                if (lAbsoluteSeekTime != ((Trackpoint) this.m_TrackToDisplay.trackpoints.get(i)).m_lTime) {
                    trackpos.m_dFraction = (((double) lAbsoluteSeekTime) - ((double) ((Trackpoint) this.m_TrackToDisplay.trackpoints.get(i)).m_lTime)) / ((double) (((Trackpoint) this.m_TrackToDisplay.trackpoints.get(i)).m_lTime - ((Trackpoint) this.m_TrackToDisplay.trackpoints.get(i - 1)).m_lTime));
                }
            } else if (lAbsoluteSeekTime != ((Trackpoint) this.m_TrackToDisplay.trackpoints.get(i)).m_lTime) {
                trackpos.m_dFraction = (((double) lAbsoluteSeekTime) - ((double) ((Trackpoint) this.m_TrackToDisplay.trackpoints.get(i)).m_lTime)) / ((double) (((Trackpoint) this.m_TrackToDisplay.trackpoints.get(i + 1)).m_lTime - ((Trackpoint) this.m_TrackToDisplay.trackpoints.get(i)).m_lTime));
            }
        }
        return trackpos;
    }

    private void DrawTimeLine(Canvas canvas, Rect rectTarget, TrackPosition Start, TrackPosition End) {
        Paint pFill = new Paint();
        Paint pText = new Paint();
        Rect rectText = new Rect();
        long lTime = 0;
        pFill.setColor(-5181264);
        pFill.setStyle(Style.FILL);
        pText.setColor(-530554784);
        pText.setTextSize((float) (this.m_Parent.m_iDisplayDensity / 12));
        pText.setFakeBoldText(true);
        pText.setAntiAlias(true);
        canvas.drawRect(rectTarget, pFill);
        pText.getTextBounds("00:00:00", 0, 8, rectText);
        long lDeltaTime = (End.m_lTime - Start.m_lTime) / ((long) this.m_iVertLinesCount);
        if (this.m_TrackToDisplay.trackpoints.size() > 0) {
            lTime = Start.m_lTime - ((Trackpoint) this.m_TrackToDisplay.trackpoints.get(0)).m_lTime;
        }
        for (int i = 0; i < this.m_iVertLinesCount; i++) {
            long lTime2 = lTime / 1000;
            Canvas canvas2 = canvas;
            canvas2.drawText(String.format("%02d:%02d:%02d", new Object[]{Long.valueOf(lTime2 / 3600), Long.valueOf((lTime2 % 3600) / 60), Long.valueOf((lTime2 % 3600) % 60)}), (float) (((rectTarget.width() / this.m_iVertLinesCount) * i) + 3), (float) ((rectTarget.top + (rectTarget.height() / 2)) + (rectText.height() / 2)), pText);
            lTime += lDeltaTime;
        }
    }

    private void DrawGrid(Canvas canvas, Rect rectTarget, double dStart, double dEnd, int iType) {
        int i;
        Paint pHatch = new Paint();
        Paint pText = new Paint();
        String sText = null;
        Rect rectText = new Rect();
        DashPathEffect dash = new DashPathEffect(new float[]{4.0f, 4.0f}, 0.0f);
        pHatch.setColor(-1870100344);
        pHatch.setStyle(Style.STROKE);
        pHatch.setStrokeWidth(1.0f);
        pHatch.setAntiAlias(true);
        pHatch.setPathEffect(dash);
        pText.setColor(-530554784);
        pText.setTextSize((float) (this.m_Parent.m_iDisplayDensity / 12));
        pText.setFakeBoldText(true);
        pText.setAntiAlias(true);
        for (i = 0; i < this.m_iVertLinesCount; i++) {
            canvas.drawLine((float) ((rectTarget.width() / this.m_iVertLinesCount) * i), (float) rectTarget.top, (float) ((rectTarget.width() / this.m_iVertLinesCount) * i), (float) (rectTarget.bottom + this.m_iScaleHeight), pHatch);
        }
        double dDelta = (this.m_dMaxY - this.m_dMinY) / ((double) this.m_iHorLinesCount);
        int iUnitIndex = MMTrackerActivity.m_SettingsUnitsDistances;
        for (i = 0; i < this.m_iHorLinesCount; i++) {
            canvas.drawLine((float) rectTarget.left, (float) ((rectTarget.height() / this.m_iHorLinesCount) * i), (float) rectTarget.right, (float) ((rectTarget.height() / this.m_iHorLinesCount) * i), pHatch);
            if (iType == MMTrackerActivity.GRAPH_TYPE_SPEED) {
                sText = String.format("%.1f %s", new Object[]{Double.valueOf(this.m_dMaxY - (((double) (i + 1)) * dDelta)), Tools.m_sUnitSpeed[iUnitIndex]});
            } else if (iType == MMTrackerActivity.GRAPH_TYPE_HEIGHT) {
                sText = String.format("%.1f %s", new Object[]{Double.valueOf(this.m_dMaxY - (((double) (i + 1)) * dDelta)), Tools.m_sUnitHeight[iUnitIndex]});
            }
            if (sText != null) {
                canvas.drawText(sText, (float) (((double) rectTarget.left) + (((double) rectTarget.width()) * 0.02d)), (float) (((double) ((rectTarget.height() / this.m_iHorLinesCount) * (i + 1))) - (((double) rectTarget.height()) * 0.025d)), pText);
            }
        }
        pText.setTextSize((float) (this.m_Parent.m_iDisplayDensity / 8));
        if (iType == MMTrackerActivity.GRAPH_TYPE_SPEED) {
            pText.getTextBounds(this.m_Parent.getString(C0047R.string.TrackGrapActivity_title_velocity), 0, this.m_Parent.getString(C0047R.string.TrackGrapActivity_title_velocity).length(), rectText);
            canvas.drawText(this.m_Parent.getString(C0047R.string.TrackGrapActivity_title_velocity), (float) ((getWidth() / 2) - (rectText.width() / 2)), (float) (((double) rectText.height()) * 1.5d), pText);
        }
        if (iType == MMTrackerActivity.GRAPH_TYPE_HEIGHT) {
            pText.getTextBounds(this.m_Parent.getString(C0047R.string.TrackGrapActivity_title_altitude), 0, this.m_Parent.getString(C0047R.string.TrackGrapActivity_title_altitude).length(), rectText);
            canvas.drawText(this.m_Parent.getString(C0047R.string.TrackGrapActivity_title_altitude), (float) ((getWidth() / 2) - (rectText.width() / 2)), (float) (((double) rectText.height()) * 1.5d), pText);
        }
    }

    private void DrawGraph(Canvas canvas, Rect rectTarget, TrackPosition Start, TrackPosition End, int iType, int c) {
        Paint pGraph = new Paint();
        Paint pGraphFill = new Paint();
        Path path = new Path();
        ArrayList<Trackpoint> trackpoints = this.m_TrackToDisplay.trackpoints;
        pGraph.setColor(-16777216 | c);
        pGraph.setStyle(Style.STROKE);
        pGraph.setStrokeWidth(2.0f);
        pGraph.setAntiAlias(true);
        pGraphFill.setStyle(Style.FILL);
        pGraphFill.setColor(1342177280 | c);
        pGraphFill.setAntiAlias(true);
        long lTotalTimeSpan = End.m_lTime - Start.m_lTime;
        if (lTotalTimeSpan != 0 && trackpoints.size() >= 2) {
            double diff;
            double dFactor = ((double) rectTarget.height()) / (this.m_dMaxY - this.m_dMinY);
            double dXpos = (double) rectTarget.left;
            double dYTempOld = 0.0d;
            boolean bFirst = true;
            double dMin = this.m_dMinY;
            double dUnitSpeed = Tools.m_dUnitSpeedFactor[MMTrackerActivity.m_SettingsUnitsDistances];
            double dUnitHeight = Tools.m_dUnitHeightFactor[MMTrackerActivity.m_SettingsUnitsDistances];
            double dYTemp = (double) rectTarget.bottom;
            if (iType == MMTrackerActivity.GRAPH_TYPE_SPEED) {
                diff = ((Trackpoint) trackpoints.get(Start.m_iIndex + 1)).m_dVelocity - ((Trackpoint) trackpoints.get(Start.m_iIndex)).m_dVelocity;
                dYTemp = ((Trackpoint) trackpoints.get(Start.m_iIndex)).m_dVelocity + (Start.m_dFraction * diff);
                dYTemp = ((double) rectTarget.height()) - (((dYTemp * dUnitSpeed) - dMin) * dFactor);
            }
            if (iType == MMTrackerActivity.GRAPH_TYPE_HEIGHT) {
                diff = ((Trackpoint) trackpoints.get(Start.m_iIndex + 1)).m_dAltitude - ((Trackpoint) trackpoints.get(Start.m_iIndex)).m_dAltitude;
                dYTemp = ((Trackpoint) trackpoints.get(Start.m_iIndex)).m_dAltitude + (Start.m_dFraction * diff);
                dYTemp = ((double) rectTarget.height()) - (((dYTemp * dUnitHeight) - dMin) * dFactor);
            }
            path.reset();
            path.moveTo((float) rectTarget.left, (float) rectTarget.bottom);
            path.lineTo((float) rectTarget.left, (float) dYTemp);
            for (int i = Start.m_iIndex + 1; i <= End.m_iIndex; i++) {
                double d;
                long lNewTime;
                double d2;
                double d3;
                dYTemp = (double) rectTarget.bottom;
                if (i == End.m_iIndex) {
                    if (End.m_dFraction != 0.0d) {
                        if (iType == MMTrackerActivity.GRAPH_TYPE_SPEED) {
                            diff = ((Trackpoint) trackpoints.get(End.m_iIndex)).m_dVelocity - ((Trackpoint) trackpoints.get(End.m_iIndex - 1)).m_dVelocity;
                            dYTemp = ((Trackpoint) trackpoints.get(End.m_iIndex)).m_dVelocity + (End.m_dFraction * diff);
                            dYTemp = ((double) rectTarget.height()) - (((dYTemp * dUnitSpeed) - dMin) * dFactor);
                        }
                        if (iType == MMTrackerActivity.GRAPH_TYPE_HEIGHT) {
                            diff = ((Trackpoint) trackpoints.get(End.m_iIndex)).m_dAltitude - ((Trackpoint) trackpoints.get(End.m_iIndex - 1)).m_dAltitude;
                            dYTemp = ((Trackpoint) trackpoints.get(End.m_iIndex)).m_dAltitude + (End.m_dFraction * diff);
                            dYTemp = ((double) rectTarget.height()) - (((dYTemp * dUnitHeight) - dMin) * dFactor);
                        }
                        if (bFirst) {
                            d = (double) 1;
                            dYTemp = ((((double) 0) * dYTempOld) + dYTemp) / r0;
                        } else {
                            bFirst = false;
                        }
                        if (i != End.m_iIndex) {
                            lNewTime = End.m_lTime;
                        } else {
                            lNewTime = ((Trackpoint) trackpoints.get(i)).m_lTime;
                        }
                        d2 = (double) rectTarget.left;
                        d = (double) rectTarget.width();
                        d3 = (double) lTotalTimeSpan;
                        path.lineTo((float) (r0 + ((r0 / r0) * ((double) (lNewTime - Start.m_lTime)))), (float) dYTemp);
                        dYTempOld = dYTemp;
                    }
                }
                if (iType == MMTrackerActivity.GRAPH_TYPE_SPEED) {
                    dYTemp = ((double) rectTarget.height()) - (((((Trackpoint) trackpoints.get(i)).m_dVelocity * dUnitSpeed) - dMin) * dFactor);
                }
                if (iType == MMTrackerActivity.GRAPH_TYPE_HEIGHT) {
                    dYTemp = ((double) rectTarget.height()) - (((((Trackpoint) trackpoints.get(i)).m_dAltitude * dUnitHeight) - dMin) * dFactor);
                }
                if (bFirst) {
                    d = (double) 1;
                    dYTemp = ((((double) 0) * dYTempOld) + dYTemp) / r0;
                } else {
                    bFirst = false;
                }
                if (i != End.m_iIndex) {
                    lNewTime = ((Trackpoint) trackpoints.get(i)).m_lTime;
                } else {
                    lNewTime = End.m_lTime;
                }
                d2 = (double) rectTarget.left;
                d = (double) rectTarget.width();
                d3 = (double) lTotalTimeSpan;
                path.lineTo((float) (r0 + ((r0 / r0) * ((double) (lNewTime - Start.m_lTime)))), (float) dYTemp);
                dYTempOld = dYTemp;
            }
            path.lineTo((float) rectTarget.right, (float) rectTarget.bottom);
            canvas.drawPath(path, pGraphFill);
            canvas.drawPath(path, pGraph);
        }
    }

    private void DrawOutlineAndHandles(Canvas canvas) {
        int iWidth = getWidth();
        int iHeight = getHeight();
        int iZoomHeight = (int) Math.round((this.m_dZoomCtrlYRatio / 100.0d) * ((double) iHeight));
        Paint pOutline = new Paint();
        Paint pHandleFill = new Paint();
        Paint pHandleDarkFill = new Paint();
        Paint pHandleLine = new Paint();
        Paint pGray = new Paint();
        Rect r = new Rect();
        RectF rectHandleLeft = new RectF();
        RectF rectHandleRight = new RectF();
        int iHandleWidth = this.m_iHandleWidth;
        int iHandleLeftPosAbs = (int) Math.round(((((double) (iWidth - iHandleWidth)) * this.m_dHandleLeftPos) / 100.0d) + ((double) (iHandleWidth / 2)));
        int iHandleRightPosAbs = (int) Math.round(((((double) (iWidth - iHandleWidth)) * this.m_dHandleRightPos) / 100.0d) + ((double) (iHandleWidth / 2)));
        r.set(2, 2, iWidth - 2, iHeight - 2);
        rectHandleLeft.set((float) Math.round((float) (iHandleLeftPosAbs - (iHandleWidth / 2))), (float) (r.bottom - ((iZoomHeight / 10) * 7)), (float) Math.round((float) ((iHandleWidth / 2) + iHandleLeftPosAbs)), (float) (r.bottom - ((iZoomHeight / 10) * 3)));
        rectHandleRight.set((float) Math.round((float) (iHandleRightPosAbs - (iHandleWidth / 2))), (float) (r.bottom - ((iZoomHeight / 10) * 7)), (float) Math.round((float) ((iHandleWidth / 2) + iHandleRightPosAbs)), (float) (r.bottom - ((iZoomHeight / 10) * 3)));
        pOutline.setColor(Color.argb(255, 80, 80, 80));
        pOutline.setStyle(Style.STROKE);
        pOutline.setStrokeWidth((float) 4);
        pHandleFill.setStyle(Style.FILL);
        pHandleFill.setColor(-4473925);
        pHandleFill.setAntiAlias(true);
        pHandleDarkFill.setStyle(Style.FILL);
        pHandleDarkFill.setColor(-8947849);
        pHandleDarkFill.setAntiAlias(true);
        pHandleLine.setStyle(Style.STROKE);
        pHandleLine.setColor(-11513776);
        pHandleLine.setStrokeWidth(2.0f);
        pHandleLine.setAntiAlias(true);
        pGray.setColor(1428300322);
        pGray.setStyle(Style.FILL);
        canvas.drawRect(new Rect(r.left - 4, r.bottom - iZoomHeight, iHandleLeftPosAbs, r.bottom + 4), pGray);
        canvas.drawRect(new Rect(iHandleRightPosAbs, r.bottom - iZoomHeight, r.right + 4, r.bottom + 4), pGray);
        canvas.drawLine((float) r.left, (float) r.top, (float) r.right, (float) r.top, pOutline);
        canvas.drawLine((float) r.right, (float) r.top, (float) r.right, (float) (r.bottom - iZoomHeight), pOutline);
        canvas.drawLine((float) r.right, (float) (r.bottom - iZoomHeight), (float) iHandleRightPosAbs, (float) (r.bottom - iZoomHeight), pOutline);
        canvas.drawLine((float) iHandleRightPosAbs, (float) (r.bottom - iZoomHeight), (float) iHandleRightPosAbs, (float) r.bottom, pOutline);
        canvas.drawLine((float) iHandleRightPosAbs, (float) r.bottom, (float) iHandleLeftPosAbs, (float) r.bottom, pOutline);
        canvas.drawLine((float) iHandleLeftPosAbs, (float) r.bottom, (float) iHandleLeftPosAbs, (float) (r.bottom - iZoomHeight), pOutline);
        canvas.drawLine((float) iHandleLeftPosAbs, (float) (r.bottom - iZoomHeight), (float) r.left, (float) (r.bottom - iZoomHeight), pOutline);
        canvas.drawLine((float) r.left, (float) (r.bottom - iZoomHeight), (float) r.left, (float) r.top, pOutline);
        canvas.drawRoundRect(rectHandleLeft, 5.0f, 5.0f, pHandleFill);
        canvas.drawRoundRect(rectHandleLeft, 5.0f, 5.0f, pHandleLine);
        rectHandleLeft.inset(rectHandleLeft.width() / 4.0f, rectHandleLeft.width() / 4.0f);
        canvas.drawRect(rectHandleLeft, pHandleDarkFill);
        canvas.drawRoundRect(rectHandleRight, 5.0f, 5.0f, pHandleFill);
        canvas.drawRoundRect(rectHandleRight, 5.0f, 5.0f, pHandleLine);
        rectHandleRight.inset(rectHandleRight.width() / 4.0f, rectHandleRight.width() / 4.0f);
        canvas.drawRect(rectHandleRight, pHandleDarkFill);
    }
}
