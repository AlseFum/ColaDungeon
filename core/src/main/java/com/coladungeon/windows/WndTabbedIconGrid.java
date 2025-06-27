package com.coladungeon.windows;

import java.util.ArrayList;
import java.util.List;

import com.coladungeon.Chrome;
import com.coladungeon.scenes.PixelScene;
import com.coladungeon.ui.OptionSlider;
import com.coladungeon.ui.RedButton;
import com.coladungeon.ui.RenderedTextBlock;
import com.coladungeon.ui.StyledButton;
import com.coladungeon.ui.Window;
import com.watabou.noosa.Image;

public class WndTabbedIconGrid extends Window {
    
    private static final int ICON_SIZE = 16;
    private static final int MARGIN = 2;
    private static final int GAP = 4;
    private static final int TAB_HEIGHT = 20;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SLIDER_HEIGHT = 20;
    private static final int MESSAGE_HEIGHT = 30;
    private static final int MIN_WINDOW_WIDTH = 120;
    private static final int MAX_WINDOW_WIDTH = 240;
    
    private RenderedTextBlock title;
    private List<TabData> tabs;
    private List<StyledButton> tabButtons;
    private RenderedTextBlock message;
    private OptionSlider slider;
    private RedButton confirmBtn;
    private int currentTab = 0;
    private final int cols;
    private List<StyledButton> gridButtons;
    
    public static class IconItem {
        public Image icon;
        public String message;
        public boolean enabled;
        public Runnable onClick;
        
        public IconItem(Image icon, String message, Runnable onClick) {
            this.icon = icon;
            this.message = message;
            this.enabled = true;
            this.onClick = onClick;
        }
    }
    
    public static class TabData {
        public String name;
        public Image icon;
        public List<IconItem> iconItems;
        public boolean hasSlider;
        public float sliderMin = 0;
        public float sliderMax = 100;
        public float sliderValue = 50;
        public String sliderLabel = "数值";
        public Runnable sliderChangeCallback;
        
        public TabData(String name, Image icon) {
            this.name = name;
            this.icon = icon;
            this.iconItems = new ArrayList<>();
            this.hasSlider = false;
        }
    }
    
    public static class Builder {
        private String title = "选择物品";
        private int cols = 4;
        private List<TabData> tabs = new ArrayList<>();
        
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
        
        public Builder setColumns(int cols) {
            this.cols = cols;
            return this;
        }
        
        public Builder addTab(String name, Image icon) {
            tabs.add(new TabData(name, icon));
            return this;
        }
        
        public Builder addItemToTab(int tabIndex, Image icon, String message, Runnable onClick) {
            if (tabIndex >= 0 && tabIndex < tabs.size()) {
                tabs.get(tabIndex).iconItems.add(new IconItem(icon, message, onClick));
            }
            return this;
        }
        
        public Builder setTabSlider(int tabIndex, boolean hasSlider) {
            if (tabIndex >= 0 && tabIndex < tabs.size()) {
                tabs.get(tabIndex).hasSlider = hasSlider;
            }
            return this;
        }
        
        public Builder setTabSliderRange(int tabIndex, float min, float max) {
            if (tabIndex >= 0 && tabIndex < tabs.size()) {
                TabData tab = tabs.get(tabIndex);
                tab.sliderMin = min;
                tab.sliderMax = max;
            }
            return this;
        }
        
        public Builder setTabSliderValue(int tabIndex, float value) {
            if (tabIndex >= 0 && tabIndex < tabs.size()) {
                tabs.get(tabIndex).sliderValue = value;
            }
            return this;
        }
        
        public Builder setTabSliderLabel(int tabIndex, String label) {
            if (tabIndex >= 0 && tabIndex < tabs.size()) {
                tabs.get(tabIndex).sliderLabel = label;
            }
            return this;
        }
        
        public Builder setTabSliderChangeCallback(int tabIndex, Runnable callback) {
            if (tabIndex >= 0 && tabIndex < tabs.size()) {
                tabs.get(tabIndex).sliderChangeCallback = callback;
            }
            return this;
        }
        
        public WndTabbedIconGrid build() {
            return new WndTabbedIconGrid(this);
        }
    }
    
    public WndTabbedIconGrid(Builder builder) {
        super();
        this.cols = builder.cols;
        this.tabs = builder.tabs;
        this.tabButtons = new ArrayList<>();
        this.gridButtons = new ArrayList<>();
        
        // 计算最大行数和窗口尺寸
        int maxRows = 0;
        for (TabData tab : tabs) {
            int rows = (int)Math.ceil(tab.iconItems.size() / (float)cols);
            maxRows = Math.max(maxRows, rows);
        }
        
        // 计算窗口宽度 - 添加最大宽度限制
        int gridWidth = cols * ICON_SIZE + (cols - 1) * GAP;
        int calculatedWidth = gridWidth + MARGIN * 2;
        int width = Math.max(MIN_WINDOW_WIDTH, Math.min(calculatedWidth, MAX_WINDOW_WIDTH));
        
        // 添加标题
        title = PixelScene.renderTextBlock(builder.title, 8);
        title.maxWidth(width - MARGIN * 2); // 限制标题宽度
        title.setPos((width - title.width()) / 2, MARGIN);
        add(title);
        
        // 计算tab宽度 - 确保tab不会太宽
        float tabWidth = Math.min((width - MARGIN * 2) / (float)tabs.size(), 60); // 限制单个tab最大宽度为60
        float totalTabWidth = tabWidth * tabs.size();
        float tabStartX = (width - totalTabWidth) / 2; // 居中显示tabs
        
        // 添加tab按钮
        float tabX = tabStartX;
        float tabY = title.bottom() + GAP;
        
        for (int i = 0; i < tabs.size(); i++) {
            final int tabIndex = i;
            TabData tab = tabs.get(i);
            
            StyledButton tabBtn = new StyledButton(Chrome.Type.GREY_BUTTON, "") {
                @Override
                protected void onClick() {
                    switchTab(tabIndex);
                }
            };
            tabBtn.setRect(tabX, tabY, tabWidth - 1, TAB_HEIGHT);
            tabBtn.icon(tab.icon);
            add(tabBtn);
            tabButtons.add(tabBtn);
            
            tabX += tabWidth;
        }
        
        // 创建网格区域 - 预先创建足够的按钮
        float gridY = tabY + TAB_HEIGHT + GAP;
        for (int i = 0; i < cols * maxRows; i++) {
            final int index = i;
            StyledButton btn = new StyledButton(Chrome.Type.BLANK, "") {
                @Override
                protected void onClick() {
                    handleGridClick(index);
                }
            };
            btn.setSize(ICON_SIZE, ICON_SIZE);
            btn.visible = false; // 初始隐藏
            add(btn);
            gridButtons.add(btn);
        }
        
        // 添加消息文本
        message = PixelScene.renderTextBlock("请选择一个物品", 6);
        message.maxWidth(width - MARGIN * 2);
        float messageY = gridY + maxRows * ICON_SIZE + (maxRows - 1) * GAP + GAP;
        message.setPos((width - message.width()) / 2, messageY); // 水平居中
        add(message);
        
        // 添加slider（创建但初始隐藏）
        slider = new OptionSlider("", "0", "100", 0, 10) {
            @Override
            protected void onChange() {
                handleSliderChange();
            }
        };
        float sliderY = message.bottom() + GAP;
        slider.setRect(MARGIN, sliderY, width - MARGIN * 2, SLIDER_HEIGHT);
        slider.visible = false;
        add(slider);
        
        // 添加确认按钮
        confirmBtn = new RedButton("确定") {
            @Override
            public void onClick() {
                hide();
            }
        };
        float buttonY = sliderY + SLIDER_HEIGHT + GAP;
        confirmBtn.setRect(MARGIN, buttonY, width - MARGIN * 2, BUTTON_HEIGHT);
        add(confirmBtn);
        
        // 调整窗口大小
        resize(width, (int)(confirmBtn.bottom() + MARGIN));
        
        // 显示第一个tab
        if (!tabs.isEmpty()) {
            switchTab(0);
        }
    }
    
    private void switchTab(int tabIndex) {
        if (tabIndex < 0 || tabIndex >= tabs.size()) return;
        
        currentTab = tabIndex;
        TabData tab = tabs.get(tabIndex);
        
        // 更新tab按钮状态
        for (int i = 0; i < tabButtons.size(); i++) {
            StyledButton btn = tabButtons.get(i);
            if (i == tabIndex) {
                // 选中的标签：保持原图标，使用高亮效果
                btn.icon(tabs.get(i).icon);
                btn.alpha(1.0f);
                btn.textColor(0xFFFFFF);  // 白色文字
            } else {
                // 未选中的标签：原图标，半透明效果
                btn.icon(tabs.get(i).icon);
                btn.alpha(0.6f);
                btn.textColor(0x888888);  // 灰色文字
            }
        }
        
        // 隐藏所有网格按钮
        for (StyledButton btn : gridButtons) {
            btn.visible = false;
        }
        
        // 显示当前tab的网格项，居中显示
        int itemsCount = tab.iconItems.size();
        int rows = (int)Math.ceil(itemsCount / (float)cols);
        int lastRowCols = itemsCount % cols == 0 ? cols : itemsCount % cols;
        
        // 使用实际可用宽度来计算网格布局
        float availableWidth = width - MARGIN * 2;
        float actualGridWidth = Math.min(cols * ICON_SIZE + (cols - 1) * GAP, availableWidth);
        float startY = tabButtons.get(0).bottom() + GAP;
        float y = startY;
        
        for (int row = 0; row < rows; row++) {
            // 计算当前行的列数
            int currentRowCols = (row == rows - 1) ? lastRowCols : cols;
            
            // 计算当前行的宽度
            float rowWidth = currentRowCols * ICON_SIZE + (currentRowCols - 1) * GAP;
            
            // 计算水平居中的起始X坐标
            float startX = (width - rowWidth) / 2;
            float x = startX;
            
            for (int col = 0; col < currentRowCols; col++) {
                int i = row * cols + col;
                if (i < tab.iconItems.size() && i < gridButtons.size()) {
                    StyledButton btn = gridButtons.get(i);
                    IconItem item = tab.iconItems.get(i);
                    
                    btn.icon(item.icon);
                    btn.setPos(x, y);
                    btn.enable(item.enabled);
                    btn.visible = true;
                    
                    x += ICON_SIZE + GAP;
                }
            }
            
            y += ICON_SIZE + GAP;
        }
        
        // 更新slider显示
        if (tab.hasSlider) {
            slider.visible = true;
            slider.setSelectedValue((int)tab.sliderValue);
        } else {
            slider.visible = false;
        }
        
        // 重置消息并居中
        message.text("请选择一个物品");
        message.setPos((width - message.width()) / 2, message.top());
    }
    
    private void handleGridClick(int index) {
        TabData tab = tabs.get(currentTab);
        if (index >= 0 && index < tab.iconItems.size()) {
            IconItem item = tab.iconItems.get(index);
            if (item.enabled) {
                message.text(item.message);
                message.setPos((width - message.width()) / 2, message.top()); // 更新后重新居中
                if (item.onClick != null) {
                    item.onClick.run();
                }
            }
        }
    }
    
    private void handleSliderChange() {
        TabData tab = tabs.get(currentTab);
        if (tab.hasSlider && tab.sliderChangeCallback != null) {
            tab.sliderValue = slider.getSelectedValue();
            tab.sliderChangeCallback.run();
        }
    }
    
    public float getSliderValue(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < tabs.size()) {
            return tabs.get(tabIndex).sliderValue;
        }
        return 0;
    }
} 