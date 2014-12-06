#ifdef GL_ES
   #define LOWP lowp
   precision mediump float;
#else
   #define LOWP
#endif

uniform sampler2D uTexture;
uniform vec2 resolution;
varying vec2 vTextureCoord;

void main(void) {
	float blurSize = 1.0 / resolution.y;
	vec4 sum = vec4(0.0);	
	
	sum += texture2D(uTexture, vec2(vTextureCoord.x - 4.0 * blurSize, vTextureCoord.y)) * 0.05;
	sum += texture2D(uTexture, vec2(vTextureCoord.x - 3.0 * blurSize, vTextureCoord.y)) * 0.09;
	sum += texture2D(uTexture, vec2(vTextureCoord.x - 2.0 * blurSize, vTextureCoord.y)) * 0.12;
	sum += texture2D(uTexture, vec2(vTextureCoord.x - blurSize, vTextureCoord.y)) * 0.15;
	sum += texture2D(uTexture, vec2(vTextureCoord.x, vTextureCoord.y)) * 0.16;
	sum += texture2D(uTexture, vec2(vTextureCoord.x + blurSize, vTextureCoord.y)) * 0.15;
	sum += texture2D(uTexture, vec2(vTextureCoord.x + 2.0 * blurSize, vTextureCoord.y)) * 0.12;
	sum += texture2D(uTexture, vec2(vTextureCoord.x + 3.0 * blurSize, vTextureCoord.y)) * 0.09;
	sum += texture2D(uTexture, vec2(vTextureCoord.x + 4.0 * blurSize, vTextureCoord.y)) * 0.05;
	
	sum.a = 1.0;
	gl_FragColor = sum;
}