/*
 * Copyright © 2022 Paulo Villela. All rights reserved.
 * Use of this source code is governed by the MIT license
 * that can be found in the LICENSE file.
 */

package tryout.moduleconfig.pushtofunction.foosfl.boot

import tryout.moduleconfig.pushtofunction.foosfl.FooSflCfgInfo
import tryout.moduleconfig.pushtofunction.foosfl.FooSflCfgSrc
import tryout.moduleconfig.pushtofunction.foosfl.FooSflT
import tryout.moduleconfig.pushtofunction.foosfl.fooSflC
import tryout.moduleconfig.pushtofunction.fwk.AppCfgInfo

fun fooSflBoot(appCfg: () -> AppCfgInfo): FooSflT {
	val fooSflCfgSrc = FooSflCfgSrc(
		{ fooSflCfgAdapter(appCfg()) },
		barBfBoot(appCfg)
	)
	return fooSflC(fooSflCfgSrc)
}

fun fooSflCfgAdapter(appCfg: AppCfgInfo): FooSflCfgInfo {
	return FooSflCfgInfo(
		appCfg.x,
	)
}
