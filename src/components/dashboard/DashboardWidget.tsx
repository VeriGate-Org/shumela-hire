import React, { useState, ReactNode } from 'react';
import {
  EllipsisVerticalIcon,
  ArrowsPointingOutIcon,
  ArrowsPointingInIcon,
  Cog6ToothIcon,
  EyeSlashIcon,
  TrashIcon,
  ArrowPathIcon,
} from '@heroicons/react/24/outline';

interface DashboardWidgetProps {
  id: string;
  title: string;
  subtitle?: string;
  children: ReactNode;
  className?: string;
  loading?: boolean;
  error?: string;
  refreshable?: boolean;
  configurable?: boolean;
  removable?: boolean;
  collapsible?: boolean;
  resizable?: boolean;
  size?: 'small' | 'medium' | 'large';
  onRefresh?: () => void;
  onConfigure?: () => void;
  onRemove?: () => void;
  onResize?: (size: 'small' | 'medium' | 'large') => void;
  lastUpdated?: Date;
}

const DashboardWidget: React.FC<DashboardWidgetProps> = ({
  id,
  title,
  subtitle,
  children,
  className = '',
  loading = false,
  error,
  refreshable = false,
  configurable = false,
  removable = false,
  collapsible = false,
  resizable = false,
  size = 'medium',
  onRefresh,
  onConfigure,
  onRemove,
  onResize,
  lastUpdated,
}) => {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [showMenu, setShowMenu] = useState(false);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const handleRefresh = async () => {
    if (!onRefresh) return;
    
    setIsRefreshing(true);
    try {
      await onRefresh();
    } finally {
      setIsRefreshing(false);
    }
  };

  const getSizeClasses = () => {
    switch (size) {
      case 'small':
        return 'col-span-1';
      case 'large':
        return 'col-span-2 row-span-2';
      default:
        return 'col-span-1';
    }
  };

  const getHeightClass = () => {
    if (isCollapsed) return 'h-auto';
    
    switch (size) {
      case 'small':
        return 'h-64';
      case 'large':
        return 'h-96';
      default:
        return 'h-80';
    }
  };

  return (
    <div className={`bg-white rounded-lg border border-gray-200 shadow-sm ${getSizeClasses()} ${getHeightClass()} ${className}`}>
      {/* Widget Header */}
      <div className="flex items-center justify-between p-4 border-b border-gray-200">
        <div className="flex-1 min-w-0">
          <h3 className="text-lg font-semibold text-gray-900 truncate">{title}</h3>
          {subtitle && (
            <p className="text-sm text-gray-500 truncate mt-1">{subtitle}</p>
          )}
          {lastUpdated && (
            <p className="text-xs text-gray-400 mt-1">
              Updated {lastUpdated.toLocaleTimeString()}
            </p>
          )}
        </div>

        {/* Widget Actions */}
        <div className="flex items-center gap-2 ml-4">
          {refreshable && (
            <button
              onClick={handleRefresh}
              disabled={isRefreshing}
              className="p-1 text-gray-400 hover:text-gray-600 rounded transition-colors disabled:animate-spin"
              title="Refresh"
            >
              <ArrowPathIcon className="w-4 h-4" />
            </button>
          )}

          {/* Menu Dropdown */}
          {(configurable || removable || resizable || collapsible) && (
            <div className="relative">
              <button
                onClick={() => setShowMenu(!showMenu)}
                className="p-1 text-gray-400 hover:text-gray-600 rounded transition-colors"
              >
                <EllipsisVerticalIcon className="w-4 h-4" />
              </button>

              {showMenu && (
                <div className="absolute right-0 top-8 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-50">
                  {collapsible && (
                    <button
                      onClick={() => {
                        setIsCollapsed(!isCollapsed);
                        setShowMenu(false);
                      }}
                      className="flex items-center gap-2 w-full px-3 py-2 text-sm text-gray-700 hover:bg-gray-50"
                    >
                      {isCollapsed ? (
                        <ArrowsPointingOutIcon className="w-4 h-4" />
                      ) : (
                        <ArrowsPointingInIcon className="w-4 h-4" />
                      )}
                      {isCollapsed ? 'Expand' : 'Collapse'}
                    </button>
                  )}

                  {resizable && (
                    <>
                      <div className="border-t border-gray-100 my-1"></div>
                      <div className="px-3 py-1">
                        <p className="text-xs font-medium text-gray-500 uppercase">Size</p>
                      </div>
                      {['small', 'medium', 'large'].map((sizeOption) => (
                        <button
                          key={sizeOption}
                          onClick={() => {
                            onResize?.(sizeOption as any);
                            setShowMenu(false);
                          }}
                          className={`flex items-center justify-between w-full px-3 py-2 text-sm ${
                            size === sizeOption
                              ? 'text-blue-600 bg-blue-50'
                              : 'text-gray-700 hover:bg-gray-50'
                          }`}
                        >
                          <span className="capitalize">{sizeOption}</span>
                          {size === sizeOption && (
                            <div className="w-2 h-2 bg-blue-600 rounded-full"></div>
                          )}
                        </button>
                      ))}
                    </>
                  )}

                  {configurable && (
                    <>
                      <div className="border-t border-gray-100 my-1"></div>
                      <button
                        onClick={() => {
                          onConfigure?.();
                          setShowMenu(false);
                        }}
                        className="flex items-center gap-2 w-full px-3 py-2 text-sm text-gray-700 hover:bg-gray-50"
                      >
                        <Cog6ToothIcon className="w-4 h-4" />
                        Configure
                      </button>
                    </>
                  )}

                  {removable && (
                    <>
                      <div className="border-t border-gray-100 my-1"></div>
                      <button
                        onClick={() => {
                          onRemove?.();
                          setShowMenu(false);
                        }}
                        className="flex items-center gap-2 w-full px-3 py-2 text-sm text-red-600 hover:bg-red-50"
                      >
                        <TrashIcon className="w-4 h-4" />
                        Remove
                      </button>
                    </>
                  )}
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Widget Content */}
      {!isCollapsed && (
        <div className="p-4 flex-1 overflow-hidden">
          {loading ? (
            <div className="flex items-center justify-center h-32">
              <div className="animate-spin rounded-full h-8 w-8 border-2 border-blue-600 border-t-transparent"></div>
            </div>
          ) : error ? (
            <div className="flex items-center justify-center h-32">
              <div className="text-center">
                <div className="text-red-500 mb-2">
                  <EyeSlashIcon className="w-8 h-8 mx-auto" />
                </div>
                <p className="text-sm text-red-600">{error}</p>
                {refreshable && (
                  <button
                    onClick={handleRefresh}
                    className="mt-2 text-sm text-blue-600 hover:text-blue-800"
                  >
                    Try again
                  </button>
                )}
              </div>
            </div>
          ) : (
            <div className="h-full overflow-auto">
              {children}
            </div>
          )}
        </div>
      )}

      {/* Click outside handler */}
      {showMenu && (
        <div
          className="fixed inset-0 z-40"
          onClick={() => setShowMenu(false)}
        />
      )}
    </div>
  );
};

export default DashboardWidget;
