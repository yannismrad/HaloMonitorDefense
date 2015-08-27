precision mediump float;

varying vec3 lightVec[2];
varying vec3 eyeVec;
varying vec2 texCoord;

uniform sampler2D colorMap;

uniform vec3 diffuseColors[8];
uniform vec3 specularColors[8];
uniform vec4 ambientColor;


void main ()
{
	vec4 baseColor = texture2D(colorMap, texCoord);
		
	gl_FragColor = baseColor;
}