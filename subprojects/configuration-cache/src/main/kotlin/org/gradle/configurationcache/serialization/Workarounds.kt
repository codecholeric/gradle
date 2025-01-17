/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.configurationcache.serialization

import java.lang.reflect.Field


internal
object Workarounds {

    private
    val ignoredBeanFields: List<Pair<String, String>> = listOf(
        // TODO:configuration-cache remove once fixed
        "ndkLocation" to "com.android.build.gradle.tasks.ShaderCompile"
    )

    fun isIgnoredBeanField(field: Field) =
        ignoredBeanFields.contains(field.name to field.declaringClass.name)

    fun canReadSystemProperty(from: String): Boolean {
        return isBuildScanPlugin(from) && !shouldDisableInputWorkaroundsFor("systemProps")
    }

    fun canReadEnvironmentVariable(from: String): Boolean {
        return isBuildScanPlugin(from) && !shouldDisableInputWorkaroundsFor("envVars")
    }

    fun canStartExternalProcesses(from: String): Boolean {
        return isEnterpriseConventionsPlugin(from) && !shouldDisableInputWorkaroundsFor("processes")
    }

    fun canReadFiles(from: String): Boolean {
        return isBuildScanPlugin(from) && !shouldDisableInputWorkaroundsFor("files")
    }

    private
    fun isBuildScanPlugin(from: String): Boolean {
        return from.startsWith("com.gradle.scan.plugin.internal.")
    }

    // TODO(https://github.com/gradle/gradle-org-conventions-plugin/issues/18) Remove the workaround when our conventions plugin is compatible.
    private
    fun isEnterpriseConventionsPlugin(from: String): Boolean {
        return from.startsWith("com.gradle.enterprise.conventions.")
    }

    private
    fun shouldDisableInputWorkaroundsFor(area: String): Boolean {
        return System.getProperty("org.gradle.internal.disable.input.workarounds")?.contains(area, ignoreCase = true) ?: false
    }
}
