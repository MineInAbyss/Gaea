package org.cultofclang.minecraft.gaea

import org.cultofclang.utils.MarketBook
import org.cultofclang.utils.calcMarket

val Broker: MarketBook by lazy { calcMarket() }
