package com.integraone.util;

import java.awt.*;

/**
 * UI Constants for consistent styling across the application
 */
public class UIConstants {
    // Colors
    public static final Color BACKGROUND_COLOR = new Color(234, 246, 255); // #EAF6FF
    public static final Color CONTENT_COLOR = Color.WHITE; // #FFFFFF
    public static final Color BUTTON_COLOR = new Color(75, 123, 236); // #4B7BEC
    public static final Color TEXT_COLOR = new Color(26, 26, 46); // #1A1A2E
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113); // #2ECC71
    public static final Color ERROR_COLOR = new Color(231, 76, 60); // #E74C3C
    public static final Color WARNING_COLOR = new Color(241, 196, 15); // #F1C40F
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 12);
    public static final Font TABLE_FONT = new Font("Arial", Font.PLAIN, 12);
    
    // Dimensions
    public static final Dimension BUTTON_SIZE = new Dimension(120, 35);
    public static final Dimension FIELD_SIZE = new Dimension(200, 25);
    public static final Dimension LARGE_FIELD_SIZE = new Dimension(300, 25);
    
    // Spacing
    public static final int PADDING = 10;
    public static final int MARGIN = 20;
    
    // Window sizes
    public static final Dimension LOGIN_SIZE = new Dimension(400, 300);
    public static final Dimension DASHBOARD_SIZE = new Dimension(1200, 800);
    public static final Dimension PANEL_SIZE = new Dimension(1000, 700);
}