package com.kbskyblock.teams;

import com.kbskyblock.core.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    private Item item;
    private int page;
    private int defaultRank;
}
