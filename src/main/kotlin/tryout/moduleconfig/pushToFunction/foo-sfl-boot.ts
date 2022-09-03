/*
 * Copyright © 2022 Paulo Villela. All rights reserved.
 * Use of this source code is governed by the MIT license
 * that can be found in the LICENSE file.
 */

import { AppCfgInfo} from "./app-cfg";
import { fooSflC, FooSflT } from "./foo-sfl";
import { barBfBoot } from "./bar-bf-boot";

export function fooSflBoot(appCfgSrc: () => AppCfgInfo): FooSflT {
  const bar = barBfBoot(appCfgSrc)
  const cfgSrc = {
    get: appCfgSrc,
    bar
  }
  return fooSflC(cfgSrc)
}
