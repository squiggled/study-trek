/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  darkMode: 'class',
  theme: {
    extend: {},
  },
  plugins: [require("daisyui")],
  daisyui: {
    styled: true,
    themes: true,
    base: true,
    utils: true,
    logs: true,
    rtl: false,
    themes: [
      {
        mytheme: {
          "primary": "#7480ff", 
          "secondary": "#ff52d9",
          "accent": "#00cdb7",
          "neutral": "#2a323c",
          "base-100": "#1d232a",
          "info": "#00b5ff",
          "success": "#9affdc",
          "warning": "#ffbe00",
          "error": "#ff5861"
        },
      },
    ],
  },
}
