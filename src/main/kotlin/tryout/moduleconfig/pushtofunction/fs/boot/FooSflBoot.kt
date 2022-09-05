/*
 * Copyright © 2022 Paulo Villela. All rights reserved.
 * Use of this source code is governed by the MIT license
 * that can be found in the LICENSE file.
 */

package tryout.moduleconfig.pushtofunction.fs.boot

import tryout.moduleconfig.pushtofunction.fs.FooSflCfgInfo
import tryout.moduleconfig.pushtofunction.fs.FooSflCfgSrc
import tryout.moduleconfig.pushtofunction.fs.FooSflT
import tryout.moduleconfig.pushtofunction.fs.fooSflC
import tryout.moduleconfig.pushtofunction.fwk.AppCfgInfo

fun fooSflBoot(appCfg: () -> AppCfgInfo): FooSflT {
	val fooSflCfgSrc = FooSflCfgSrc(
		get = { fooSflCfgAdapter(appCfg()) },
		bar = barBfBoot(appCfg)
	)
	return fooSflC(fooSflCfgSrc)
}

fun fooSflCfgAdapter(appCfg: AppCfgInfo): FooSflCfgInfo {
	return FooSflCfgInfo(
		x = appCfg.x,
	)
}
