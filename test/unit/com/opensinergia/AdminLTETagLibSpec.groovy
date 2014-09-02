package com.opensinergia

import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.test.mixin.TestMixin

import org.grails.plugin.platform.NavigationTagLib
import org.grails.plugin.platform.navigation.NavigationItem 
import org.grails.plugin.platform.UiExtensionsTagLib

/**
 * Unit tests for AdminLTE taglib
 *
 * @author Rodolfo Castellanos
 */
@TestFor(AdminLTETagLib)
@Mock([NavigationTagLib, UiExtensionsTagLib])
class AdminLTETagLibSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test empty menu"() {
        given: "an empty navigation item"
        def emptyNode = new NavigationItem([
            linkArgs: [url: '/']
        ])
        and: "a minimal grails navigation containing the empty item"
        def grailsNavigation = [:]
        grailsNavigation.nodesForPath = { String path ->
            [emptyNode]
        }
        grailsNavigation.nodeForId = { String id ->
            emptyNode
        }
        and: "a NavigationTagLib bean"
        def navTagLib = applicationContext.getBean(NavigationTagLib)
        when: "the minimal grails navigation is attached to the NavigationTagLib"
        navTagLib.grailsNavigation = grailsNavigation
        then: "the sidebarMenu tag generates an empty menu"
        tagLib.sidebarMenu(scope: 'emptyapp', path: 'emptypath').toString() == '<ul class="sidebar-menu"></ul>'
        assert applyTemplate('<altt:sidebarMenu scope="emptyapp" path="emptypath"/>') == '<ul class="sidebar-menu"></ul>'
    }

    void "test menu with only one entry"() {
        given: "a single navigation item with its title"
        def itemTitle = 'Dummy Title'
        def singleNode = new NavigationItem([
            name: 'Dummy Menu Entry',
            linkArgs: [controller: 'DummyController', action: 'dummyAction'],
            titleDefault: itemTitle,
        ])
        def parentNode = new NavigationItem([
            linkArgs: [controller: 'DummyController'],
            children: [singleNode]
        ])
        and: "a minimal grails navigation containing the single item"
        def grailsNavigation = [:]
        grailsNavigation.nodesForPath = { String path ->
            [parentNode]
        }
        grailsNavigation.nodeForId = { String id ->
            parentNode
        }
        and: "a NavigationTagLib bean"
        def navTagLib = applicationContext.getBean(NavigationTagLib)
        and: "a UiExtensionsTagLib bean"
        def pTagLib = applicationContext.getBean(UiExtensionsTagLib)
        when: "the minimal grails navigation is attached to the NavigationTagLib"
        navTagLib.grailsNavigation = grailsNavigation
        then: "the sidebarMenu tag generates a menu with only one entry"
        def expected = '<ul class="sidebar-menu"><li><a href="/dummyController/dummyAction"><i class="fa"></i><span>' + itemTitle + '</span></a></li></ul>'
        tagLib.sidebarMenu(scope: 'singleapp', path: 'singlepath').toString() == expected
        assert applyTemplate('<altt:sidebarMenu scope="singleapp" path="singlepath"/>') == expected
    }

    void "test menu with only one entry that is active and has additional data"() {
        given: "a single navigation item with its title and custom data"
        def itemTitle = 'Dummy Title With Data'
        def singleNode = new NavigationItem([
            name: 'Dummy Menu Entry With Data',
            linkArgs: [controller: 'DummyController', action: 'dummyAction'],
            titleDefault: itemTitle,
            data: [faIcon: 'fa-dummy']
        ])
        def parentNode = new NavigationItem([
            linkArgs: [controller: 'DummyController'],
            children: [singleNode]
        ])
        and: "a minimal grails navigation containing the single item assumed as active"
        def grailsNavigation = [:]
        // returned items by nodesForPath are assumed as active
        grailsNavigation.nodesForPath = { String path ->
            [parentNode, singleNode]
        }
        grailsNavigation.nodeForId = { String id ->
            parentNode
        }
        and: "a NavigationTagLib bean"
        def navTagLib = applicationContext.getBean(NavigationTagLib)
        and: "a UiExtensionsTagLib bean"
        def pTagLib = applicationContext.getBean(UiExtensionsTagLib)
        when: "the minimal grails navigation is attached to the NavigationTagLib"
        navTagLib.grailsNavigation = grailsNavigation
        then: "the sidebarMenu tag generates a menu with only one entry that is active and has additional data"
        def expected = '<ul class="sidebar-menu"><li class="active"><a href="/dummyController/dummyAction"><i class="fa fa-dummy"></i><span>' + itemTitle + '</span></a></li></ul>'
        tagLib.sidebarMenu(scope: 'singleapp', path: 'whatever').toString() == expected
        assert applyTemplate('<altt:sidebarMenu scope="singleapp" path="whatever"/>') == expected
    }

    void "test menu with two entries with only one active"() {
        given: "two navigation items"
        def firstItemTitle = 'First Entry Title'
        def firstNode = new NavigationItem([
            name: 'First Menu Entry',
            linkArgs: [controller: 'DummyController', action: 'firstAction'],
            titleDefault: firstItemTitle
        ])
        def secondItemTitle = 'Second Entry Title'
        def secondNode = new NavigationItem([
            name: 'Second Menu Entry',
            linkArgs: [controller: 'DummyController', action: 'secondAction'],
            titleDefault: secondItemTitle
        ])
        def parentNode = new NavigationItem([
            linkArgs: [controller: 'DummyController'],
            children: [firstNode, secondNode]
        ])
        and: "a minimal grails navigation containing the two items with only the second one active"
        def grailsNavigation = [:]
        // returned items by nodesForPath are assumed as active
        grailsNavigation.nodesForPath = { String path ->
            [parentNode, secondNode]
        }
        grailsNavigation.nodeForId = { String id ->
            parentNode
        }
        and: "a NavigationTagLib bean"
        def navTagLib = applicationContext.getBean(NavigationTagLib)
        and: "a UiExtensionsTagLib bean"
        def pTagLib = applicationContext.getBean(UiExtensionsTagLib)
        when: "the minimal grails navigation is attached to the NavigationTagLib"
        navTagLib.grailsNavigation = grailsNavigation
        then: "the sidebarMenu tag generates a menu with two entries and only the second one is active"
        def expected = '<ul class="sidebar-menu"><li><a href="/dummyController/firstAction"><i class="fa"></i><span>' + firstItemTitle + '</span></a></li>' + '<li class="active"><a href="/dummyController/secondAction"><i class="fa"></i><span>' + secondItemTitle + '</span></a></li></ul>'
        tagLib.sidebarMenu(scope: 'singleapp', path: 'dummyController/secondAction').toString() == expected
        assert applyTemplate('<altt:sidebarMenu scope="singleapp" path="dummyController/secondAction"/>') == expected
    }
}