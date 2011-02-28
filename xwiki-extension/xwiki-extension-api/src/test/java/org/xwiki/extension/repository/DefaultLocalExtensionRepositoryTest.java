/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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
package org.xwiki.extension.repository;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.extension.Extension;
import org.xwiki.extension.ExtensionId;
import org.xwiki.extension.ResolveException;
import org.xwiki.extension.repository.internal.DefaultLocalExtensionRepository;
import org.xwiki.extension.test.ConfigurableDefaultCoreExtensionRepository;
import org.xwiki.extension.test.RepositoryUtil;
import org.xwiki.test.AbstractComponentTestCase;

public class DefaultLocalExtensionRepositoryTest extends AbstractComponentTestCase
{
    private LocalExtensionRepository localExtensionRepository;

    private RepositoryUtil repositoryUtil;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();

        this.repositoryUtil = new RepositoryUtil(getClass().getSimpleName(), getConfigurationSource());
        this.repositoryUtil.setup();

        // lookup

        this.localExtensionRepository =
            (DefaultLocalExtensionRepository) getComponentManager().lookup(LocalExtensionRepository.class);
    }

    @Override
    protected void registerComponents() throws Exception
    {
        super.registerComponents();

        ConfigurableDefaultCoreExtensionRepository.register(getComponentManager());
    }

    @Test
    public void testInit()
    {
        Assert.assertEquals(2, this.localExtensionRepository.countExtensions());
    }

    @Test
    public void testGetLocalExtension()
    {
        Assert.assertNull(this.localExtensionRepository.getInstalledExtension("unexistingextension", null));

        Extension extension = this.localExtensionRepository.getInstalledExtension("existingextension", null);

        Assert.assertNotNull(extension);
        Assert.assertEquals("existingextension", extension.getId().getId());
        Assert.assertEquals("version", extension.getId().getVersion());
        Assert.assertEquals("type", extension.getType());
        Assert.assertEquals("existingextensiondependency", extension.getDependencies().get(0).getId());
        Assert.assertEquals("version", extension.getDependencies().get(0).getVersion());
    }

    @Test
    public void testResolve() throws ResolveException
    {
        try {
            this.localExtensionRepository.resolve(new ExtensionId("unexistingextension", "version"));

            Assert.fail("Resolve should have failed");
        } catch (ResolveException expected) {
            // expected
        }

        try {
            this.localExtensionRepository.resolve(new ExtensionId("existingextension", "wrongversion"));

            Assert.fail("Resolve should have failed");
        } catch (ResolveException expected) {
            // expected
        }

        Extension extension = this.localExtensionRepository.resolve(new ExtensionId("existingextension", "version"));

        Assert.assertNotNull(extension);
        Assert.assertEquals("existingextension", extension.getId().getId());
        Assert.assertEquals("version", extension.getId().getVersion());
    }
}
