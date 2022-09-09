/*
 *  Copyright © 2021 Paulo Villela. All rights reserved.
 *  Use of this source code is governed by the Apache 2.0 license
 *  that can be found in the LICENSE file.
 */

package tryout.moduleconfig.pushtovar.startup

import tryout.moduleconfig.pushtovar.fs.barBfAdapterCfgSrc
import tryout.moduleconfig.pushtovar.fs.fooSflAdapterCfgSrc

fun initialize()  {
	val c = ::getAppConfiguration
	fooSflAdapterCfgSrc.set(c)
	barBfAdapterCfgSrc.set(c)
}
