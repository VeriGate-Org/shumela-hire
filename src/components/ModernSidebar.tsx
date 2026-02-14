import React, { useState, useEffect, useMemo } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useAuth } from '../contexts/AuthContext';
import { roleConfigurations } from '../config/roleConfig';
import {
  HomeIcon,
  UsersIcon,
  BriefcaseIcon,
  ChartBarIcon,
  DocumentTextIcon,
  CalendarIcon,
  CogIcon,
  BuildingOfficeIcon,
  MagnifyingGlassIcon,
  ChevronRightIcon,
  Squares2X2Icon,
  ClipboardDocumentListIcon,
  AcademicCapIcon,
  GlobeAltIcon,
  ShieldCheckIcon,
  XMarkIcon
} from '@heroicons/react/24/outline';
import {
  HomeIcon as HomeIconSolid,
  UsersIcon as UsersIconSolid,
  BriefcaseIcon as BriefcaseIconSolid,
  ChartBarIcon as ChartBarIconSolid,
  DocumentTextIcon as DocumentTextIconSolid,
  CalendarIcon as CalendarIconSolid
} from '@heroicons/react/24/solid';

interface NavigationItem {
  id: string;
  label: string;
  href: string;
  icon: React.ComponentType<any>;
  iconSolid?: React.ComponentType<any>;
  badge?: string;
  children?: NavigationItem[];
}

interface ModernSidebarProps {
  isCollapsed?: boolean;
  onToggleCollapse?: () => void;
}

const ModernSidebar: React.FC<ModernSidebarProps> = ({
  isCollapsed = false,
  onToggleCollapse
}) => {
  const pathname = usePathname();
  const { user } = useAuth();
  const [expandedMenus, setExpandedMenus] = useState<string[]>(['dashboard', 'talent']);
  const [searchQuery, setSearchQuery] = useState('');
  const [filteredItems, setFilteredItems] = useState<NavigationItem[]>([]);

  const getIconForNavItem = (label: string, href: string) => {
    if (href.includes('/dashboard') || label.toLowerCase().includes('dashboard')) return { icon: HomeIcon, iconSolid: HomeIconSolid };
    if (label.toLowerCase().includes('analytics') || label.toLowerCase().includes('reports')) return { icon: ChartBarIcon, iconSolid: ChartBarIconSolid };
    if (label.toLowerCase().includes('job') || label.toLowerCase().includes('posting')) return { icon: BriefcaseIcon, iconSolid: BriefcaseIconSolid };
    if (href.includes('/internal/jobs')) return { icon: BuildingOfficeIcon };
    if (label.toLowerCase().includes('applicant') || label.toLowerCase().includes('candidate') || label.toLowerCase().includes('user')) return { icon: UsersIcon, iconSolid: UsersIconSolid };
    if (label.toLowerCase().includes('application')) return { icon: DocumentTextIcon, iconSolid: DocumentTextIconSolid };
    if (label.toLowerCase().includes('interview') || label.toLowerCase().includes('schedule')) return { icon: CalendarIcon, iconSolid: CalendarIconSolid };
    if (label.toLowerCase().includes('pipeline') || label.toLowerCase().includes('workflow')) return { icon: Squares2X2Icon };
    if (label.toLowerCase().includes('management') || label.toLowerCase().includes('admin')) return { icon: CogIcon };
    if (label.toLowerCase().includes('audit') || label.toLowerCase().includes('logs')) return { icon: ClipboardDocumentListIcon };
    return { icon: DocumentTextIcon };
  };

  const navigationItems = useMemo(() => {
    if (!user) return [];
    const roleConfig = roleConfigurations[user.role];
    const items: NavigationItem[] = [];
    roleConfig.navigationItems.forEach((navItem, index) => {
      const iconMapping = getIconForNavItem(navItem.label, navItem.href);
      items.push({
        id: navItem.href.replace('/', '_') + '_' + index,
        label: navItem.label,
        href: navItem.href,
        icon: iconMapping.icon,
        iconSolid: iconMapping.iconSolid,
        badge: navItem.label.toLowerCase().includes('application') ? '8' :
               navItem.label.toLowerCase().includes('interview') ? '3' :
               navItem.label.toLowerCase().includes('offer') ? '2' : undefined,
      });
    });
    return items;
  }, [user?.role]);

  const bottomNavigationItems: NavigationItem[] = [
    { id: 'training', label: 'Training', href: '/training', icon: AcademicCapIcon },
    { id: 'integrations', label: 'Integrations', href: '/integrations', icon: GlobeAltIcon },
    { id: 'security', label: 'Security', href: '/security', icon: ShieldCheckIcon },
  ];

  useEffect(() => {
    if (searchQuery) {
      const filtered = navigationItems.filter(item =>
        item.label.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (item.children && item.children.some(child =>
          child.label.toLowerCase().includes(searchQuery.toLowerCase())
        ))
      );
      setFilteredItems(filtered);
    } else {
      setFilteredItems(navigationItems);
    }
  }, [searchQuery, navigationItems]);

  const toggleMenu = (menuId: string) => {
    if (isCollapsed) return;
    setExpandedMenus(prev =>
      prev.includes(menuId) ? prev.filter(id => id !== menuId) : [...prev, menuId]
    );
  };

  const isActiveRoute = (href: string, exact = false) => {
    if (exact) return pathname === href;
    return pathname.startsWith(href) && href !== '/';
  };

  const renderNavigationItem = (item: NavigationItem, isChild = false) => {
    const isActive = isActiveRoute(item.href);
    const isExpanded = expandedMenus.includes(item.id);
    const hasChildren = item.children && item.children.length > 0;
    const IconComponent = isActive && item.iconSolid ? item.iconSolid : item.icon;

    return (
      <div key={item.id}>
        <Link
          href={!hasChildren ? item.href : '#'}
          onClick={(e) => {
            if (hasChildren) { e.preventDefault(); toggleMenu(item.id); }
          }}
          className={`
            group flex items-center gap-2.5 px-2.5 py-2 text-[13px] font-medium rounded transition-colors border border-transparent
            ${isChild
              ? isActive
                ? 'bg-violet-500/[0.06] text-violet-800 border-violet-500/40'
                : 'text-gray-500 hover:bg-gray-100 hover:text-violet-700'
              : isActive
                ? 'bg-violet-500/[0.06] text-violet-800 border-l-[3px] border-l-violet-600 border-y-transparent border-r-transparent pl-[7px]'
                : 'text-gray-600 hover:bg-gray-100 hover:text-violet-700'
            }
            ${isCollapsed ? 'justify-center px-2' : ''}
          `}
        >
          <IconComponent className={`
            h-4 w-4 flex-shrink-0
            ${isActive ? 'text-violet-600' : 'text-gray-400 group-hover:text-gray-500'}
            transition-colors
          `} />

          {!isCollapsed && (
            <>
              <span className="flex-1 text-left truncate">{item.label}</span>

              {item.badge && (
                <span className="px-1.5 py-0.5 text-[10px] rounded bg-violet-600 text-white">
                  {item.badge}
                </span>
              )}

              {hasChildren && (
                <ChevronRightIcon className={`
                  h-3 w-3 text-gray-400 transition-transform duration-200
                  ${isExpanded ? 'rotate-90' : ''}
                `} />
              )}
            </>
          )}
        </Link>

        {hasChildren && !isCollapsed && isExpanded && (
          <div className="mt-0.5 space-y-0.5 ml-4">
            {item.children?.map((child) => renderNavigationItem(child, true))}
          </div>
        )}
      </div>
    );
  };

  return (
    <aside className={`
      fixed left-0 top-14 bottom-0 bg-white border-r border-gray-200 overflow-y-auto transition-all duration-200 ease-in-out z-40
      ${isCollapsed ? 'w-16' : 'w-60'}
    `}>
      {/* Search */}
      {!isCollapsed && (
        <div className="px-3 py-3">
          <div className="relative">
            <MagnifyingGlassIcon className="absolute left-2.5 top-1/2 -translate-y-1/2 h-3.5 w-3.5 text-gray-400" />
            <input
              type="text"
              placeholder="Search..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-8 pr-3 py-1.5 text-xs border border-gray-200 rounded bg-gray-50 text-gray-700 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-violet-500/25 focus:border-transparent"
            />
            {searchQuery && (
              <button onClick={() => setSearchQuery('')} className="absolute right-2.5 top-1/2 -translate-y-1/2">
                <XMarkIcon className="h-3 w-3 text-gray-400 hover:text-gray-600" />
              </button>
            )}
          </div>
        </div>
      )}

      {/* Main Navigation */}
      <div className="px-3 py-1">
        {!isCollapsed && (
          <p className="text-[11px] text-gray-400 uppercase tracking-[0.12em] mb-2 font-semibold px-2.5">
            Navigation
          </p>
        )}
        <nav className="space-y-0.5">
          {filteredItems.map(item => renderNavigationItem(item))}
        </nav>
      </div>

      {/* Bottom section */}
      <div className="px-3 pt-4 mt-4 border-t border-gray-100">
        {!isCollapsed && (
          <p className="text-[11px] text-gray-400 uppercase tracking-[0.12em] mb-2 font-semibold px-2.5">
            System
          </p>
        )}
        <nav className="space-y-0.5">
          {bottomNavigationItems.map(item => renderNavigationItem(item))}
        </nav>
      </div>
    </aside>
  );
};

export default ModernSidebar;
