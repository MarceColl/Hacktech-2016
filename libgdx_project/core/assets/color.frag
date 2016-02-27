#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;
uniform vec3 tint;

void main() {
        vec3 color = texture2D(u_texture, v_texCoords).rgb;
        gl_FragColor = v_texCoords.x*vec4(tint, texture2D(u_texture, v_texCoords).a);
}