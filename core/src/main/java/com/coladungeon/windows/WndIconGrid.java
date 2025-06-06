package com.coladungeon.windows;

import java.util.ArrayList;
import java.util.List;

import com.coladungeon.Chrome;
import com.coladungeon.scenes.PixelScene;
import com.coladungeon.ui.Icons;
import com.coladungeon.ui.RedButton;
import com.coladungeon.ui.RenderedTextBlock;
import com.coladungeon.ui.StyledButton;
import com.coladungeon.ui.Window;
import com.watabou.noosa.Image;

public class WndIconGrid extends Window {
    
    private static final int ICON_SIZE = 16;
    private static final int MARGIN = 2;
    private static final int GAP = 4;
    private static final int MESSAGE_HEIGHT = 40; // 消息区域高度
    private static final int BUTTON_HEIGHT = 20; // 按钮高度
    
    private RenderedTextBlock title;
    private RenderedTextBlock message;
    private RedButton confirmBtn;
    private List<IconItem> iconItems;
    private final int cols;
    private final int rows;
    private int selectedIndex = -1;
    
    public static class IconItem {
        private Image icon;
        private String message;
        private boolean enabled;
        private Runnable onClick;
        
        public IconItem(Image icon, String message, Runnable onClick) {
            this.icon = icon;
            this.message = message;
            this.enabled = true;
            this.onClick = onClick;
        }
        
        public IconItem setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }
    }
    
    public static class Builder {
        private String title = "选择药水";
        private int cols = 4;
        private List<IconItem> iconItems = new ArrayList<>();
        
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
        
        public Builder setColumns(int cols) {
            this.cols = cols;
            return this;
        }
        
        public Builder addItem(Image icon, String message, Runnable onClick) {
            iconItems.add(new IconItem(icon, message, onClick));
            return this;
        }
        
        public WndIconGrid build() {
            return new WndIconGrid(this);
        }
    }
    
    private WndIconGrid(Builder builder) {
        super();
        this.cols = builder.cols;
        this.iconItems = builder.iconItems;
        this.rows = (int)Math.ceil(iconItems.size() / (float)cols);
        
        // 计算窗口大小
        int width = cols * (ICON_SIZE + GAP) + MARGIN * 2;
        
        // 添加标题
        title = PixelScene.renderTextBlock(builder.title, 8);
        title.setPos((width - title.width()) / 2, MARGIN);
        add(title);
        
        // 创建网格
        float x = MARGIN;
        float y = title.bottom() + GAP;
        
        for (int i = 0; i < iconItems.size(); i++) {
            final int index = i;
            IconItem item = iconItems.get(i);
            StyledButton btn = 
                new StyledButton(Chrome.Type.BLANK, "") {
                @Override
                protected void onClick() {
                    if (item.enabled) {
                        selectedIndex = index;
                        updateMessage(index);
                    }
                }
                
                @Override
                protected void onPointerDown() {
                    // 移除点击效果
                }
                
                @Override
                protected void onPointerUp() {
                    // 移除点击效果
                }
            };
            btn.icon(item.icon);
            btn.setSize(ICON_SIZE, ICON_SIZE);
            btn.setPos(x, y);
            btn.enable(item.enabled);
            add(btn);
            
            x += ICON_SIZE + GAP;
            if ((i + 1) % cols == 0) {
                x = MARGIN;
                y += ICON_SIZE + GAP;
            }
        }
        
        // 添加消息文本
        message = PixelScene.renderTextBlock("", 6);
        message.maxWidth(width - MARGIN * 2);
        message.setPos(MARGIN, y + GAP * 2);
        add(message);
        
        // 添加确定按钮
        confirmBtn = new RedButton("确定") {
            @Override
            protected void onClick() {
                if (selectedIndex >= 0 && selectedIndex < iconItems.size()) {
                    IconItem item = iconItems.get(selectedIndex);
                    if (item.enabled && item.onClick != null) {
                        item.onClick.run();
                    }
                }
                hide();
            }
        };
        confirmBtn.setRect(MARGIN, message.bottom() + GAP, width - MARGIN * 2, BUTTON_HEIGHT);
        add(confirmBtn);
        
        Image icon = Icons.get(Icons.SKULL);
        add(icon);

        // 根据实际内容调整窗口大小
        resize(width, (int)(confirmBtn.bottom() + MARGIN));
    }
    
    public void setIcon(int index, Image icon) {
        if (index >= 0 && index < iconItems.size()) {
            iconItems.get(index).icon = icon;
            updateMessage(index);
        }
    }
    
    public void enable(int index, boolean enabled) {
        if (index >= 0 && index < iconItems.size()) {
            iconItems.get(index).enabled = enabled;
        }
    }
    
    private void updateMessage(int index) {
        if (index >= 0 && index < iconItems.size()) {
            IconItem item = iconItems.get(index);
            message.text(item.message);
            message.setPos((width - message.width()) / 2, message.top());
            
            // 更新按钮位置
            confirmBtn.setRect(MARGIN, message.bottom() + GAP, width - MARGIN * 2, BUTTON_HEIGHT);
            
            // 调整窗口大小
            resize(width, (int)(confirmBtn.bottom() + MARGIN));
        }
    }
} 