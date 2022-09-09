/*
 *  Copyright © 2021 Paulo Villela. All rights reserved.
 *  Use of this source code is governed by the Apache 2.0 license
 *  that can be found in the LICENSE file.
 */

package tryout.moduleconfig.pushtovar.fs

import tryout.moduleconfig.pullwithpushoverride.fwk.CfgSrc
import tryout.moduleconfig.pullwithpushoverride.fwk.makeConfigSource

data class BarBfCfgInfo(
	val z: Int
)

val barBfCfgSrc: CfgSrc<BarBfCfgInfo> = makeConfigSource()

fun barBf() {
	println(barBfCfgSrc.get().z)
}
