precision mediump float;

varying vec3 lightVec[2];
varying vec3 eyeVec;
varying vec2 texCoord;

uniform sampler2D colorMap;
uniform sampler2D noiseMap;
uniform float time;
uniform float baseSpeed;
uniform float noiseScale;
uniform float invRadius;
uniform float alpha;

uniform vec3 diffuseColors[8];
uniform vec3 specularColors[8];
uniform vec4 ambientColor;


float coef = 10.0;

void main ()
{
	//Calcul de la vitesse et de la direction du défilement de la texture d'eau
	vec2 uvTimeShift = texCoord + vec2( -0.4, 0.5 ) * time * baseSpeed; //Défilement de la texture
	vec4 noise = texture2D( noiseMap, uvTimeShift );
	vec2 uvNoisyTimeShift = texCoord + noiseScale * vec2( noise.r, noise.g ); //quantité de noise de la texture
	vec4 baseColor = texture2D( colorMap, uvNoisyTimeShift );
	baseColor.a = alpha;
	
		
		//Illumination de phong
		/*vec3 L = normalize(lightVec-position * 0.2);
		vec3 R = reflect(-L, normal);
		vec3 V = normalize(eyeVec - position);
		
	 	float diffuse = max(dot(normal, L),0.0);
		float speculaire = (max(dot(R,V),0.5));
		float spec = pow(speculaire,coef);
		
		vec3 phong = (diffuse + ( 1.0 - diffuse) + (spec)); */
		
		gl_FragColor = baseColor;
}