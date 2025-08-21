import React from 'react';
import {
  LineChart,
  Line,
  AreaChart,
  Area,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  RadialBarChart,
  RadialBar,
  ComposedChart,
} from 'recharts';

// Color palette for consistent chart theming
export const CHART_COLORS = {
  primary: '#3b82f6',
  secondary: '#8b5cf6',
  success: '#10b981',
  warning: '#f59e0b',
  danger: '#ef4444',
  info: '#06b6d4',
  gray: '#6b7280',
  light: '#e5e7eb',
  dark: '#374151',
} as const;

export const CHART_COLOR_PALETTE = [
  CHART_COLORS.primary,
  CHART_COLORS.secondary,
  CHART_COLORS.success,
  CHART_COLORS.warning,
  CHART_COLORS.danger,
  CHART_COLORS.info,
  '#f472b6', // pink
  '#a78bfa', // purple
  '#34d399', // emerald
  '#fbbf24', // amber
];

// Common chart props interface
interface BaseChartProps {
  data: any[];
  height?: number;
  showGrid?: boolean;
  showLegend?: boolean;
  animated?: boolean;
  className?: string;
}

// Line Chart Component
interface LineChartProps extends BaseChartProps {
  xKey: string;
  yKey: string;
  color?: string;
  strokeWidth?: number;
  showDots?: boolean;
  curved?: boolean;
}

export const RecruitmentLineChart: React.FC<LineChartProps> = ({
  data,
  xKey,
  yKey,
  height = 300,
  color = CHART_COLORS.primary,
  strokeWidth = 2,
  showGrid = true,
  showLegend = false,
  showDots = true,
  curved = true,
  animated = true,
  className = '',
}) => {
  return (
    <div className={`w-full ${className}`}>
      <ResponsiveContainer width="100%" height={height}>
        <LineChart data={data} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
          {showGrid && <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />}
          <XAxis 
            dataKey={xKey} 
            stroke="#6b7280"
            fontSize={12}
          />
          <YAxis 
            stroke="#6b7280"
            fontSize={12}
          />
          <Tooltip 
            contentStyle={{
              backgroundColor: 'white',
              border: '1px solid #e5e7eb',
              borderRadius: '8px',
              boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
            }}
          />
          {showLegend && <Legend />}
          <Line
            type={curved ? "monotone" : "linear"}
            dataKey={yKey}
            stroke={color}
            strokeWidth={strokeWidth}
            dot={showDots ? { fill: color, r: 4 } : false}
            animationDuration={animated ? 1500 : 0}
            activeDot={{ r: 6 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

// Area Chart Component
interface AreaChartProps extends BaseChartProps {
  xKey: string;
  yKey: string;
  color?: string;
  fillOpacity?: number;
}

export const RecruitmentAreaChart: React.FC<AreaChartProps> = ({
  data,
  xKey,
  yKey,
  height = 300,
  color = CHART_COLORS.primary,
  fillOpacity = 0.6,
  showGrid = true,
  showLegend = false,
  animated = true,
  className = '',
}) => {
  return (
    <div className={`w-full ${className}`}>
      <ResponsiveContainer width="100%" height={height}>
        <AreaChart data={data} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
          {showGrid && <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />}
          <XAxis dataKey={xKey} stroke="#6b7280" fontSize={12} />
          <YAxis stroke="#6b7280" fontSize={12} />
          <Tooltip 
            contentStyle={{
              backgroundColor: 'white',
              border: '1px solid #e5e7eb',
              borderRadius: '8px',
              boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
            }}
          />
          {showLegend && <Legend />}
          <Area
            type="monotone"
            dataKey={yKey}
            stroke={color}
            fill={color}
            fillOpacity={fillOpacity}
            animationDuration={animated ? 1500 : 0}
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
};

// Bar Chart Component
interface BarChartProps extends BaseChartProps {
  xKey: string;
  yKey: string;
  color?: string;
  horizontal?: boolean;
}

export const RecruitmentBarChart: React.FC<BarChartProps> = ({
  data,
  xKey,
  yKey,
  height = 300,
  color = CHART_COLORS.primary,
  showGrid = true,
  showLegend = false,
  horizontal = false,
  animated = true,
  className = '',
}) => {
  const ChartComponent = horizontal ? BarChart : BarChart;
  
  return (
    <div className={`w-full ${className}`}>
      <ResponsiveContainer width="100%" height={height}>
        <ChartComponent 
          data={data} 
          margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
          layout={horizontal ? 'horizontal' : 'vertical'}
        >
          {showGrid && <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />}
          <XAxis 
            dataKey={horizontal ? yKey : xKey}
            type={horizontal ? 'number' : 'category'}
            stroke="#6b7280"
            fontSize={12}
          />
          <YAxis 
            dataKey={horizontal ? xKey : undefined}
            type={horizontal ? 'category' : 'number'}
            stroke="#6b7280"
            fontSize={12}
          />
          <Tooltip 
            contentStyle={{
              backgroundColor: 'white',
              border: '1px solid #e5e7eb',
              borderRadius: '8px',
              boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
            }}
          />
          {showLegend && <Legend />}
          <Bar
            dataKey={yKey}
            fill={color}
            animationDuration={animated ? 1500 : 0}
            radius={[2, 2, 0, 0]}
          />
        </ChartComponent>
      </ResponsiveContainer>
    </div>
  );
};

// Pie Chart Component
interface PieChartProps extends BaseChartProps {
  dataKey: string;
  nameKey: string;
  colors?: string[];
  innerRadius?: number;
  showLabels?: boolean;
}

export const RecruitmentPieChart: React.FC<PieChartProps> = ({
  data,
  dataKey,
  nameKey,
  height = 300,
  colors = CHART_COLOR_PALETTE,
  innerRadius = 0,
  showLabels = true,
  showLegend = true,
  animated = true,
  className = '',
}) => {
  return (
    <div className={`w-full ${className}`}>
      <ResponsiveContainer width="100%" height={height}>
        <PieChart margin={{ top: 5, right: 5, left: 5, bottom: 5 }}>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            innerRadius={innerRadius}
            outerRadius={80}
            paddingAngle={5}
            dataKey={dataKey}
            animationDuration={animated ? 1500 : 0}
            label={showLabels ? ({ name, percent }) => `${name} ${((percent || 0) * 100).toFixed(0)}%` : false}
          >
            {data.map((_, index) => (
              <Cell key={`cell-${index}`} fill={colors[index % colors.length]} />
            ))}
          </Pie>
          <Tooltip 
            contentStyle={{
              backgroundColor: 'white',
              border: '1px solid #e5e7eb',
              borderRadius: '8px',
              boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
            }}
          />
          {showLegend && <Legend />}
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
};

// Radial Bar Chart (Progress/Gauge Chart)
interface RadialBarChartProps extends BaseChartProps {
  dataKey: string;
  maxValue?: number;
  color?: string;
}

export const RecruitmentRadialChart: React.FC<RadialBarChartProps> = ({
  data,
  dataKey,
  height = 250,
  maxValue = 100,
  color = CHART_COLORS.primary,
  animated = true,
  className = '',
}) => {
  return (
    <div className={`w-full ${className}`}>
      <ResponsiveContainer width="100%" height={height}>
        <RadialBarChart cx="50%" cy="50%" innerRadius="40%" outerRadius="80%" data={data}>
          <RadialBar
            dataKey={dataKey}
            cornerRadius={10}
            fill={color}
            animationDuration={animated ? 1500 : 0}
          />
          <Tooltip 
            contentStyle={{
              backgroundColor: 'white',
              border: '1px solid #e5e7eb',
              borderRadius: '8px',
              boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
            }}
          />
        </RadialBarChart>
      </ResponsiveContainer>
    </div>
  );
};

// Composed Chart (Line + Bar combination)
interface ComposedChartProps extends BaseChartProps {
  xKey: string;
  barData: { key: string; color?: string }[];
  lineData: { key: string; color?: string }[];
}

export const RecruitmentComposedChart: React.FC<ComposedChartProps> = ({
  data,
  xKey,
  barData,
  lineData,
  height = 300,
  showGrid = true,
  showLegend = true,
  animated = true,
  className = '',
}) => {
  return (
    <div className={`w-full ${className}`}>
      <ResponsiveContainer width="100%" height={height}>
        <ComposedChart data={data} margin={{ top: 20, right: 30, bottom: 20, left: 20 }}>
          {showGrid && <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />}
          <XAxis dataKey={xKey} stroke="#6b7280" fontSize={12} />
          <YAxis stroke="#6b7280" fontSize={12} />
          <Tooltip 
            contentStyle={{
              backgroundColor: 'white',
              border: '1px solid #e5e7eb',
              borderRadius: '8px',
              boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
            }}
          />
          {showLegend && <Legend />}
          {barData.map((bar, index) => (
            <Bar
              key={bar.key}
              dataKey={bar.key}
              fill={bar.color || CHART_COLOR_PALETTE[index % CHART_COLOR_PALETTE.length]}
              animationDuration={animated ? 1500 : 0}
            />
          ))}
          {lineData.map((line, index) => (
            <Line
              key={line.key}
              type="monotone"
              dataKey={line.key}
              stroke={line.color || CHART_COLOR_PALETTE[(index + barData.length) % CHART_COLOR_PALETTE.length]}
              strokeWidth={2}
              dot={{ r: 4 }}
              animationDuration={animated ? 1500 : 0}
            />
          ))}
        </ComposedChart>
      </ResponsiveContainer>
    </div>
  );
};

// Custom Tooltip Component
export const CustomTooltip = ({ active, payload, label }: any) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-white p-3 border border-gray-200 rounded-lg shadow-lg">
        <p className="font-medium text-gray-900">{`${label}`}</p>
        {payload.map((entry: any, index: number) => (
          <p key={index} className="text-sm" style={{ color: entry.color }}>
            {`${entry.name}: ${entry.value}`}
          </p>
        ))}
      </div>
    );
  }
  return null;
};
