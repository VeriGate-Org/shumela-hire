import React, { useState, useEffect, useMemo } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import RoleSwitcher from './RoleSwitcher';
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
  UserCircleIcon,
  BellIcon,
  MagnifyingGlassIcon,
  ChevronRightIcon,
  ChevronDownIcon,
  Squares2X2Icon,
  ClipboardDocumentListIcon,
  PresentationChartLineIcon,
  DocumentCheckIcon,
  AcademicCapIcon,
  GlobeAltIcon,
  ShieldCheckIcon,
  ArrowRightOnRectangleIcon
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
  badgeColor?: 'blue' | 'green' | 'yellow' | 'red' | 'purple';
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

  // Map navigation item labels to icons
  const getIconForNavItem = (label: string, href: string) => {
    // Dashboard and main items
    if (href.includes('/dashboard') || label.toLowerCase().includes('dashboard')) return { icon: HomeIcon, iconSolid: HomeIconSolid };
    if (label.toLowerCase().includes('analytics') || label.toLowerCase().includes('reports')) return { icon: ChartBarIcon, iconSolid: ChartBarIconSolid };
    
    // Job and position related
    if (label.toLowerCase().includes('job') || label.toLowerCase().includes('posting')) return { icon: BriefcaseIcon, iconSolid: BriefcaseIconSolid };
    if (href.includes('/internal/jobs')) return { icon: BuildingOfficeIcon };
    
    // People and candidates
    if (label.toLowerCase().includes('applicant') || label.toLowerCase().includes('candidate') || label.toLowerCase().includes('user')) return { icon: UsersIcon, iconSolid: UsersIconSolid };
    if (label.toLowerCase().includes('application')) return { icon: DocumentTextIcon, iconSolid: DocumentTextIconSolid };
    
    // Interview and calendar
    if (label.toLowerCase().includes('interview') || label.toLowerCase().includes('schedule')) return { icon: CalendarIcon, iconSolid: CalendarIconSolid };
    
    // Pipeline and workflow
    if (label.toLowerCase().includes('pipeline') || label.toLowerCase().includes('workflow')) return { icon: Squares2X2Icon };
    
    // Management and admin
    if (label.toLowerCase().includes('management') || label.toLowerCase().includes('admin')) return { icon: CogIcon };
    if (label.toLowerCase().includes('audit') || label.toLowerCase().includes('logs')) return { icon: ClipboardDocumentListIcon };
    
    // Default icon
    return { icon: DocumentTextIcon };
  };

  // Generate navigation items based on user role
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
        badgeColor: navItem.label.toLowerCase().includes('application') ? 'blue' :
                   navItem.label.toLowerCase().includes('interview') ? 'green' :
                   navItem.label.toLowerCase().includes('offer') ? 'yellow' : 'blue'
      });
    });
    
    return items;
  }, [user?.role]); // Only depend on the user role, not the entire user object

  const bottomNavigationItems = [
    {
      id: 'training',
      label: 'Training & Development',
      href: '/training',
      icon: AcademicCapIcon,
      iconSolid: AcademicCapIcon
    },
    {
      id: 'integrations',
      label: 'Integrations',
      href: '/integrations',
      icon: GlobeAltIcon,
      iconSolid: GlobeAltIcon
    },
    {
      id: 'security',
      label: 'Security & Compliance',
      href: '/security',
      icon: ShieldCheckIcon,
      iconSolid: ShieldCheckIcon
    }
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
  }, [searchQuery, navigationItems]); // Remove user?.role from dependencies since navigationItems already handles it

  const toggleMenu = (menuId: string) => {
    if (isCollapsed) return;
    
    setExpandedMenus(prev => 
      prev.includes(menuId) 
        ? prev.filter(id => id !== menuId)
        : [...prev, menuId]
    );
  };

  const isActiveRoute = (href: string, exact = false) => {
    if (exact) {
      return pathname === href;
    }
    return pathname.startsWith(href) && href !== '/';
  };

  const getBadgeStyles = (color: string) => {
    const styles = {
      blue: 'bg-blue-100 text-blue-600 ring-blue-500/20',
      green: 'bg-green-100 text-green-600 ring-green-500/20',
      yellow: 'bg-yellow-100 text-yellow-600 ring-yellow-500/20',
      red: 'bg-red-100 text-red-600 ring-red-500/20',
      purple: 'bg-purple-100 text-purple-600 ring-purple-500/20'
    };
    return styles[color as keyof typeof styles] || styles.blue;
  };

  const renderNavigationItem = (item: NavigationItem, isChild = false) => {
    const isActive = isActiveRoute(item.href);
    const isExpanded = expandedMenus.includes(item.id);
    const hasChildren = item.children && item.children.length > 0;
    
    const IconComponent = isActive && !isChild && item.iconSolid ? item.iconSolid : item.icon;
    
    return (
      <div key={item.id} className="relative">
        {/* Main navigation item */}
        <div className="relative">
          <Link
            href={!hasChildren ? item.href : '#'}
            onClick={(e) => {
              if (hasChildren) {
                e.preventDefault();
                toggleMenu(item.id);
              }
            }}
            className={`
              group flex items-center px-3 py-2.5 text-sm font-medium rounded-lg transition-all duration-200 ease-in-out
              ${isChild 
                ? `ml-8 ${isActive 
                    ? 'text-violet-600 bg-violet-50 border-r-2 border-violet-600'
                    : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
                  }`
                : `${isActive
                    ? 'bg-violet-600 text-white shadow-sm'
                    : 'text-gray-700 hover:bg-gray-100 hover:text-gray-900'
                  }`
              }
              ${isCollapsed ? 'justify-center px-2' : ''}
            `}
          >
            <IconComponent className={`
              ${isCollapsed ? 'h-6 w-6' : 'mr-3 h-5 w-5'}
              ${isActive && !isChild ? 'text-white' : ''}
              ${isChild && isActive ? 'text-blue-600' : ''}
              transition-colors duration-200
            `} />
            
            {!isCollapsed && (
              <>
                <span className="flex-1 text-left">{item.label}</span>
                
                {/* Badge */}
                {item.badge && (
                  <span className={`
                    inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ring-1 ring-inset
                    ${isActive && !isChild ? 'bg-white/20 text-white ring-white/30' : getBadgeStyles(item.badgeColor || 'blue')}
                  `}>
                    {item.badge}
                  </span>
                )}
                
                {/* Chevron for expandable items */}
                {hasChildren && (
                  <ChevronRightIcon className={`
                    ml-2 h-4 w-4 transition-transform duration-200
                    ${isExpanded ? 'rotate-90' : ''}
                    ${isActive ? 'text-white' : 'text-gray-400'}
                  `} />
                )}
              </>
            )}
          </Link>
          
          {/* Active indicator for collapsed sidebar */}
          {isCollapsed && isActive && !isChild && (
            <div className="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-8 bg-violet-600 rounded-r-full" />
          )}
        </div>
        
        {/* Children */}
        {hasChildren && !isCollapsed && isExpanded && (
          <div className="mt-1 space-y-1">
            {item.children?.map((child) => renderNavigationItem(child, true))}
          </div>
        )}
      </div>
    );
  };

  return (
    <div className={`
      fixed left-0 top-0 z-50 h-full bg-white border-r border-gray-200 shadow-xl transition-all duration-300 ease-in-out
      ${isCollapsed ? 'w-16' : 'w-80'}
    `}>
      <div className="flex flex-col h-full">
        {/* Header */}
        <div className={`
          flex items-center px-4 py-4 border-b border-gray-100
          ${isCollapsed ? 'justify-center' : 'justify-between'}
        `}>
          {!isCollapsed && (
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 bg-violet-600 rounded-lg flex items-center justify-center">
                  <span className="text-white font-bold text-sm">TG</span>
                </div>
              </div>
              <div className="ml-3">
                <h1 className="text-lg font-bold text-gray-900">TalentGate</h1>
                <p className="text-xs text-gray-500">Talent Management Suite</p>
              </div>
            </div>
          )}
          
          {isCollapsed && (
            <div className="w-8 h-8 bg-violet-600 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-sm">TG</span>
            </div>
          )}
          
          {onToggleCollapse && !isCollapsed && (
            <button
              onClick={onToggleCollapse}
              className="p-1 rounded-lg hover:bg-gray-100 transition-colors"
            >
              <ChevronRightIcon className="h-5 w-5 text-gray-400" />
            </button>
          )}
        </div>

        {/* Search */}
        {!isCollapsed && (
          <div className="px-4 py-3 border-b border-gray-100">
            <div className="relative">
              <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
              <input
                type="text"
                placeholder="Search..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-200 rounded-lg text-sm placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent"
              />
            </div>
          </div>
        )}

        {/* Main Navigation */}
        <div className="flex-1 overflow-y-auto py-4">
          <nav className="px-3 space-y-1">
            {filteredItems.map(item => renderNavigationItem(item))}
          </nav>
          
          {/* Bottom Navigation */}
          <div className="mt-8 px-3 pt-6 border-t border-gray-100">
            <div className="space-y-1">
              {bottomNavigationItems.map(item => renderNavigationItem(item))}
            </div>
          </div>
        </div>

        {/* User Profile */}
        {!isCollapsed && (
          <div className="border-t border-gray-100 p-4">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 bg-violet-600 rounded-full flex items-center justify-center">
                  <span className="text-white font-medium text-xs">
                    {user ? user.name.split(' ').map(n => n[0]).join('') : 'JD'}
                  </span>
                </div>
              </div>
              <div className="ml-3 flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900 truncate">
                  {user?.name || 'John Doe'}
                </p>
                <p className="text-xs text-gray-500 truncate flex items-center">
                  <span className="mr-1">{user ? roleConfigurations[user.role].logo : '👔'}</span>
                  {user?.role || 'HR Manager'}
                </p>
              </div>
              <button className="ml-2 p-1 rounded-lg hover:bg-gray-100 transition-colors">
                <ArrowRightOnRectangleIcon className="h-4 w-4 text-gray-400" />
              </button>
            </div>
          </div>
        )}

        {/* Role Switcher */}
        {!isCollapsed && <RoleSwitcher />}

        {/* Collapsed sidebar toggle */}
        {isCollapsed && onToggleCollapse && (
          <div className="p-2 border-t border-gray-100">
            <button
              onClick={onToggleCollapse}
              className="w-full p-2 rounded-lg hover:bg-gray-100 transition-colors flex items-center justify-center"
            >
              <ChevronRightIcon className="h-4 w-4 text-gray-400 rotate-180" />
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default ModernSidebar;
