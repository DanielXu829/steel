package com.cisdi.steel.jfreechart;

import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.util.TableOrder;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.*;
import java.text.NumberFormat;

public class MySpiderWebPlot extends SpiderWebPlot {
    /**
     *
     */
    private static final long serialVersionUID = 4005814203754627127L;
    private int ticks = DEFAULT_TICKS;
    private static final int DEFAULT_TICKS = 5;
    private NumberFormat format = NumberFormat.getInstance();
    private static final double PERPENDICULAR = 90;
    private static final double TICK_SCALE = 0.015;
    private int valueLabelGap = DEFAULT_GAP;
    private static final int DEFAULT_GAP = 10;
    private static final double THRESHOLD = 15;


    MySpiderWebPlot(CategoryDataset createCategoryDataset) {
        super(createCategoryDataset);
    }
//
//    @Override
//    protected void drawRadarPoly(Graphics2D g2, Rectangle2D plotArea, Point2D centre, PlotRenderingInfo info, int series, int catCount, double headH, double headW) {
//        Polygon polygon = new Polygon();
//        EntityCollection entities = null;
//        if (info != null) {
//            entities = info.getOwner().getEntityCollection();
//        }
//
//        for (int cat = 0; cat < catCount; ++cat) {
//            Number dataValue = this.getPlotValue(series, cat);
//            if (dataValue != null) {
//                double value = dataValue.doubleValue();
//                if (value >= 0.0D) {
//                    double angle = this.getStartAngle() + this.getDirection().getFactor() * (double) cat * 360.0D / (double) catCount;
//                    Point2D point = this.getWebPoint(plotArea, angle, value / super.getMaxValue());
//                    polygon.addPoint((int) point.getX(), (int) point.getY());
//                    Paint paint = this.getSeriesPaint(series);
//                    Paint outlinePaint = this.getSeriesOutlinePaint(series);
//                    Stroke outlineStroke = this.getSeriesOutlineStroke(series);
//                    Ellipse2D head = new java.awt.geom.Ellipse2D.Double(point.getX() - headW / 2.0D, point.getY() - headH / 2.0D, headW, headH);
//                    g2.setPaint(paint);
//                    g2.fill(head);
//                    g2.setStroke(outlineStroke);
//                    g2.setPaint(outlinePaint);
//                    g2.draw(head);
//                    if (entities != null) {
//                        int row;
//                        int col;
//                        if (super.getDataExtractOrder() == TableOrder.BY_ROW) {
//                            row = series;
//                            col = cat;
//                        } else {
//                            row = cat;
//                            col = series;
//                        }
//
//                        String tip = null;
//                        if (super.getToolTipGenerator() != null) {
//                            tip = super.getToolTipGenerator().generateToolTip(this.dataset, row, col);
//                        }
//
//                        String url = null;
//                        if (this.urlGenerator != null) {
//                            url = this.urlGenerator.generateURL(this.dataset, row, col);
//                        }
//
//                        Shape area = new Rectangle((int) (point.getX() - headW), (int) (point.getY() - headH), (int) (headW * 2.0D), (int) (headH * 2.0D));
//                        CategoryItemEntity entity = new CategoryItemEntity(area, tip, url, this.dataset, this.dataset.getRowKey(row), this.dataset.getColumnKey(col));
//                        entities.add(entity);
//                    }
//                }
//            }
//        }
//
//        Paint paint = this.getSeriesPaint(series);
//        g2.setPaint(paint);
//        g2.setStroke(this.getSeriesOutlineStroke(series));
//        g2.draw(polygon);
//        if (this.webFilled) {
//            g2.setComposite(AlphaComposite.getInstance(3, 0.1F));
//            g2.fill(polygon);
//            g2.setComposite(AlphaComposite.getInstance(3, this.getForegroundAlpha()));
//        }
//
//    }

    @Override
    protected void drawLabel(final Graphics2D g2, final Rectangle2D plotArea, final double value,
                             final int cat, final double startAngle, final double extent) {
        super.drawLabel(g2, plotArea, value, cat, startAngle, extent);
        final FontRenderContext frc = g2.getFontRenderContext();
        final double[] transformed = new double[2];
        final double[] transformer = new double[2];
        final Arc2D arc1 = new Arc2D.Double(plotArea, startAngle, 0, Arc2D.OPEN);
        for (int i = 1; i <= ticks; i++) {
            final Point2D point1 = arc1.getEndPoint();
            final double deltaX = plotArea.getCenterX();
            final double deltaY = plotArea.getCenterY();
            double labelX = point1.getX() - deltaX;
            double labelY = point1.getY() - deltaY;
            final double scale = ((double) i / (double) ticks);
            final AffineTransform tx = AffineTransform.getScaleInstance(scale, scale);
            final AffineTransform pointTrans = AffineTransform.getScaleInstance(scale + TICK_SCALE, scale + TICK_SCALE);
            transformer[0] = labelX;
            transformer[1] = labelY;
            pointTrans.transform(transformer, 0, transformed, 0, 1);
            final double pointX = transformed[0] + deltaX;
            final double pointY = transformed[1] + deltaY;
            tx.transform(transformer, 0, transformed, 0, 1);
            labelX = transformed[0] + deltaX;
            labelY = transformed[1] + deltaY;
            double rotated = (PERPENDICULAR);
            AffineTransform rotateTrans = AffineTransform.getRotateInstance(Math.toRadians(rotated), labelX, labelY);
            transformer[0] = pointX;
            transformer[1] = pointY;
            rotateTrans.transform(transformer, 0, transformed, 0, 1);
            final double x1 = transformed[0];
            final double y1 = transformed[1];
            rotated = (-PERPENDICULAR);
            rotateTrans = AffineTransform.getRotateInstance(Math.toRadians(rotated), labelX, labelY);
            rotateTrans.transform(transformer, 0, transformed, 0, 1);
            final Composite saveComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2.draw(new Line2D.Double(transformed[0], transformed[1], x1, y1));
            if (startAngle == this.getStartAngle()) {
                final String label = format.format(((double) i / (double) ticks) * this.getMaxValue());
                final LineMetrics lm = getLabelFont().getLineMetrics(label, frc);
                final double ascent = lm.getAscent();
                if (Math.abs(labelX - plotArea.getCenterX()) < THRESHOLD) {
                    labelX += valueLabelGap;
                    labelY += ascent / (float) 2;
                } else if (Math.abs(labelY - plotArea.getCenterY()) < THRESHOLD) {
                    labelY += valueLabelGap;
                } else if (labelX >= plotArea.getCenterX()) {
                    if (labelY < plotArea.getCenterY()) {
                        labelX += valueLabelGap;
                        labelY += valueLabelGap;
                    } else {
                        labelX -= valueLabelGap;
                        labelY += valueLabelGap;
                    }
                } else {
                    if (labelY > plotArea.getCenterY()) {
                        labelX -= valueLabelGap;
                        labelY -= valueLabelGap;
                    } else {
                        labelX += valueLabelGap;
                        labelY -= valueLabelGap;
                    }
                }
                g2.setPaint(getLabelPaint());
                g2.setFont(getLabelFont());
                g2.drawString(label, (float) labelX, (float) labelY);
            }
            g2.setComposite(saveComposite);
        }
    }
}