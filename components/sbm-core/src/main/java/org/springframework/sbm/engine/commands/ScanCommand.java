/*
 * Copyright 2021 - 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.sbm.engine.commands;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.sbm.engine.context.ProjectContext;
import org.springframework.sbm.engine.context.ProjectRootPathResolver;
import org.springframework.sbm.engine.precondition.PreconditionVerificationResult;
import org.springframework.sbm.engine.precondition.PreconditionVerifier;
import org.springframework.sbm.openrewrite.RewriteExecutionContext;
import org.springframework.sbm.project.parser.PathScanner;
import org.springframework.sbm.project.parser.ProjectContextInitializer;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class ScanCommand extends AbstractCommand<ProjectContext> {

    private static final String COMMAND_NAME = "scan";
    private final ProjectRootPathResolver projectRootPathResolver;
    private final ProjectContextInitializer projectContextInitializer;
    private final ApplicationEventPublisher eventPublisher;
    private final PathScanner pathScanner;
    private final PreconditionVerifier preconditionVerifier;

    @Deprecated
    public ScanCommand(ProjectRootPathResolver projectRootPathResolver, ProjectContextInitializer projectContextInitializer, ApplicationEventPublisher eventPublisher, PathScanner pathScanner, PreconditionVerifier preconditionVerifier) {
        super(COMMAND_NAME);
        this.projectRootPathResolver = projectRootPathResolver;
        this.projectContextInitializer = projectContextInitializer;
        this.eventPublisher = eventPublisher;
        this.pathScanner = pathScanner;
        this.preconditionVerifier = preconditionVerifier;
    }

    public ProjectContext execute(String... arguments) {
        Path projectRoot = projectRootPathResolver.getProjectRootOrDefault(arguments[0]);

        List<Resource> resources = pathScanner.scan(projectRoot);

        return projectContextInitializer.initProjectContext(projectRoot, resources);
    }

    public List<Resource> scanProjectRoot(String projectRoot) {
        Path projectRootPath = projectRootPathResolver.getProjectRootOrDefault(projectRoot);
        return pathScanner.scan(projectRootPath);
    }

    public List<Resource> scanProjectRoot(Path projectRootPath) {
       // Path projectRootPath = pro//projectRootPathResolver.getProjectRootOrDefault(projectRoot);
        return pathScanner.scan(projectRootPath);
    }

    public PreconditionVerificationResult checkPreconditions(String projectRoot, List<Resource> resources) {
        Path projectRootPath = projectRootPathResolver.getProjectRootOrDefault(projectRoot);
        return preconditionVerifier.verifyPreconditions(projectRootPath, resources);
    }
}
