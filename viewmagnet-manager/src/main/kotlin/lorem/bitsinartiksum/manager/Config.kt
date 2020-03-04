package lorem.bitsinartiksum.manager

import model.Mode

data class Config(val mode: Mode, val SimilarityThreshold: Float, val maxPoolSize: Int)