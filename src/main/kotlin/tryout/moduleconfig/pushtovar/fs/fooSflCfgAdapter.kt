/*
 * Copyright © 2022 Paulo Villela. All rights reserved.
 * Use of this source code is governed by the MIT license
 * that can be found in the LICENSE file.
 */

package tryout.moduleconfig.pushtovar.fs

import tryout.moduleconfig.pushtovar.startup.AppCfgInfo
import tryout.moduleconfig.pushtovar.fwk.makeConfigSource

fun fooSflCfgAdapter(appCfg: AppCfgInfo): FooSflCfgInfo {
	return FooSflCfgInfo(
		x = appCfg.x,
	)
}

val fooSflAdapterCfgSrc = run {
	val cfgSrc = makeConfigSource<AppCfgInfo>();
	fooSflCfgSrc.set({
		fooSflCfgAdapter(cfgSrc.get());
	});
	cfgSrc
}
