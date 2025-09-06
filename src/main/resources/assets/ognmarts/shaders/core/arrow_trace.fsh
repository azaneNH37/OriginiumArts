#version 150

uniform float GameTime;
uniform float mode;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    // 获取UV坐标并归一化到[-1,1]范围
    vec2 uv = texCoord0.xy * 2.0 - 1.0;
    
    // 创建流动效果 - 使用时间和UV坐标创建波纹状运动
    float timeFactor = GameTime * 1000.0;
    float flow = sin(uv.x * 8.0 + timeFactor) * 0.1 + 
                 cos(uv.y * 6.0 + timeFactor * 1.3) * 0.1;
    
    // 计算距离中心的距离（用于颜色渐变）
    float dist = length(uv);
    
    // 创建火焰状扰动 - 增加更多层次的扰动
    float flamePattern = sin(uv.x * 15.0 + timeFactor * 2.0) * 0.05;
    flamePattern += cos(uv.y * 12.0 + timeFactor * 1.7) * 0.05;
    flamePattern += sin(uv.x * 25.0 + timeFactor * 3.0) * 0.03;
    
    // 合并所有扰动效果
    dist += flow + flamePattern;
    
    // 核心火焰颜色（白色和金色混合）
    vec3 coreColor = mix(
        vec3(1.0, 1.0, 0.8),    // 亮白色
        vec3(1.0, 0.8, 0.3),    // 金色
        sin(timeFactor * 0.5) * 0.5 + 0.5 // 随时间在金和白之间摆动
    );
    
    // 外围火焰颜色（红色，带有透明度）
    vec3 outerColor = vec3(1.0, 0.6, 0.4); // 橙红色
    
    // 增强轮廓感 - 使用更陡峭的颜色过渡
    float colorEdge = 0.4; // 减小这个值会使颜色过渡更突然，轮廓更明显
    vec3 finalColor = mix(coreColor, outerColor, smoothstep(colorEdge, 0.8, dist));
    
    // 基础强度 - 确保整体常亮
    float baseIntensity = 0.9; // 基础亮度，确保整个火焰都可见
    
    // 中心增强强度
    float centerIntensity = 1.0 - smoothstep(0.0, 0.6, dist);
    centerIntensity = pow(centerIntensity, 0.5); // 使中心更集中
    
    // 垂直方向上的渐变，确保整个高度都有亮度
    float verticalPresence = 1.0 - smoothstep(0.0, 1.2, abs(uv.y));
    
    // 合并所有强度因素
    float intensity = baseIntensity + centerIntensity * 0.5;
    intensity *= verticalPresence;
    
    // 添加扰动到强度，但不要过度影响整体亮度
    intensity *= 1.0 + flamePattern * 0.2;
    
    // 轮廓增强 - 使用阶跃函数创建更清晰的边缘
    float edge = 0.7; // 边缘位置
    float edgeSharpness = 8.0; // 边缘锐度，值越大边缘越锐利
    
    float alpha = 1.0 - smoothstep(edge, 1.0, dist);
    // 应用锐化
    alpha = pow(alpha, 1.0/edgeSharpness);
    
    // 确保最小透明度，保持整体常亮
    alpha = max(alpha, 0.6); // 提高最小值，确保整体可见
    
    // 应用顶点颜色透明度
    alpha *= vertexColor.a;
    
    // 最终输出 - 确保颜色饱和度高
    fragColor = vec4(finalColor * intensity, alpha);
    
    // 可选：添加发光效果（如果需要更亮的效果）
    // fragColor.rgb += fragColor.rgb * 0.3 * centerIntensity;
}