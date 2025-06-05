package com.kbskyblock.teams.configs.inventories;

import com.kbskyblock.core.Background;
import com.kbskyblock.core.Item;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ConfirmationInventoryConfig extends NoItemGUI {
    /**
     * The yes item
     */
    public Item yes;
    /**
     * the no item
     */
    public Item no;

    public ConfirmationInventoryConfig(int size, String title, Background background, Item yes, Item no) {
        this.size = size;
        this.title = title;
        this.background = background;
        this.yes = yes;
        this.no = no;
    }
}
