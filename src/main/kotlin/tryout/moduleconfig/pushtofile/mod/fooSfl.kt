/*
 *  Copyright © 2021 Paulo Villela. All rights reserved.
 *  Use of this source code is governed by the Apache 2.0 license
 *  that can be found in the LICENSE file.
 */

package tryout.moduleconfig.pushtofile.mod

import tryout.moduleconfig.pushtofile.fwk.makeConfigSource

data class FooSflCfgInfo(
	val x: String
)

val fooSflCfgSrc = makeConfigSource<FooSflCfgInfo>(::fooSflCfgAdapter)

fun fooSfl() {
	println(fooSflCfgSrc.get().x)
	barBf()
}
