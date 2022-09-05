package tryout.moduleconfig.pullwithpushoverride.fs

import tryout.moduleconfig.pullwithpushoverride.fwk.makeConfigSource

fun baz() {
	println(bazCfgSrc.get().w.length);
}

val bazCfgSrc = makeConfigSource<BazCfgInfo>();

data class BazCfgInfo(
	val w: String
)
