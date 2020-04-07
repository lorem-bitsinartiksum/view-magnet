package lorem.bitsinartiksum.manager

import model.Mode

data class Config(val mode: Mode, val similarityThreshold: Float, val maxPoolSize: Int)