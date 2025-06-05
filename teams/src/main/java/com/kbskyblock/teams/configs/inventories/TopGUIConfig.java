package com.kbskyblock.teams.configs.inventories;

import com.kbskyblock.core.Background;
import com.kbskyblock.core.Item;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TopGUIConfig extends SingleItemGUI{
    public Item filler;

    public TopGUIConfig(int size, String title, Background background, Item item, Item filter) {
        super(size, title, background, item);
        this.filler = filter;
    }
}
