# Modernization Plan: OpenJDK 25 Runtime Upgrade

**Project**: JLearn (`jlearn-web`)

## Technical Framework

- **Language**: Java 11 currently; target OpenJDK 25
- **Framework**: Servlet 4.0 / JSP + JSTL 1.2 (`javax.*`), deployed on Tomcat 9
- **Build Tool**: Maven; WAR packaging
- **Database**: MySQL 8
- **Key Dependencies**: HikariCP, MySQL Connector/J, jBCrypt, JUnit 5

## Overview

Upgrade the existing Java application to the verified OpenJDK 25 LTS runtime
while preserving its current user-visible behavior and deployment model. Keep
the work limited to the Java runtime upgrade, its directly related build,
runtime, deployment, and documentation references, and compatibility changes
that are necessary for the application to continue working.

The upgrade must retain the existing Servlet/JSP application architecture,
`javax.*` APIs, WAR packaging, and Tomcat-based deployment unless testing shows
a specific compatibility change is required to run on Java 25. This plan does
not include cloud-readiness remediation, unrelated framework or service
migrations, or unrelated security work.

## Scope and Impact

- Set the Maven compilation target to Java 25 and make required build/runtime
  adjustments for that target.
- Review application and dependency compatibility on Java 25; make only
  changes needed to preserve existing behavior.
- Align directly related runtime/deployment instructions and Java-version
  documentation with OpenJDK 25.
- Run the project build and existing focused tests, plus narrow startup or
  smoke validation relevant to compatibility.

Out of scope: cloud-readiness findings unrelated to Java version, container
creation, deployment to Azure, broad dependency modernization, unrelated CVE
remediation, and behavioral or architectural redesign.

## Assessment Context

The supplied assessment identifies the project as using JDK 11 and reports
**“Java Version is not the latest LTS”** (`azure-java-version-02000`) at
`pom.xml:12`. The finding is verified against the current Maven configuration,
which sets `maven.compiler.release` to `11`. The README and
`docs/07-where-to-edit.md` also name Java 11. The requested target is OpenJDK
25, verified as the latest LTS on 2026-11-01.

## Compatibility and Validation Approach

Preserve current application functionality and the existing Servlet 4.0 /
JSP / Tomcat 9 deployment behavior. Check for Java 25 compiler/runtime
incompatibilities in application code and currently used build/runtime
dependencies, changing only what is required for a successful build and
operation. Validate the final Maven WAR build and existing tests, then perform
a focused application startup or smoke check if the available local runtime
and deployment setup permit it.

## Plan Dependencies

There is one implementation task. Build, test, and focused compatibility
validation are part of that task's completion criteria; there are no separate
cloud, security, integration-service, infrastructure, or deployment tasks.
