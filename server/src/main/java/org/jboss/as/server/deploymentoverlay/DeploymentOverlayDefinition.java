/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.server.deploymentoverlay;


import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.ModelOnlyRemoveStepHandler;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.access.management.ApplicationTypeAccessConstraintDefinition;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.descriptions.common.ControllerResolver;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.repository.ContentRepository;
import org.jboss.as.repository.DeploymentFileRepository;

/**
 * @author Stuart Douglas
 */
public class DeploymentOverlayDefinition extends SimpleResourceDefinition {

    private static final AttributeDefinition[] ATTRIBUTES = {};

    public static AttributeDefinition[] attributes() {
        return ATTRIBUTES.clone();
    }

    private final ContentRepository contentRepo;
    private final DeploymentFileRepository fileRepository;
    private final boolean domainLevel;


    public DeploymentOverlayDefinition(boolean domainLevel,ContentRepository contentRepo, DeploymentFileRepository fileRepository) {
        super(new Parameters(DeploymentOverlayModel.DEPLOYMENT_OVERRIDE_PATH,
                ControllerResolver.getResolver(ModelDescriptionConstants.DEPLOYMENT_OVERLAY))
                .setAddHandler(DeploymentOverlayAdd.INSTANCE)
                .setRemoveHandler(ModelOnlyRemoveStepHandler.INSTANCE)
                .setAccessConstraints(ApplicationTypeAccessConstraintDefinition.DEPLOYMENT));
        this.contentRepo = contentRepo;
        this.fileRepository = fileRepository;
        this.domainLevel = domainLevel;
    }

    @Override
    public void registerAttributes(final ManagementResourceRegistration resourceRegistration) {
        for (AttributeDefinition attr : ATTRIBUTES) {
            resourceRegistration.registerReadOnlyAttribute(attr, null);
        }
    }

    @Override
    public void registerChildren(ManagementResourceRegistration resourceRegistration) {
        if (contentRepo != null) {
            resourceRegistration.registerSubModel(new DeploymentOverlayContentDefinition(contentRepo, fileRepository));
        }
        if (!domainLevel) {
            resourceRegistration.registerSubModel(new DeploymentOverlayDeploymentDefinition());
        }
    }
}
