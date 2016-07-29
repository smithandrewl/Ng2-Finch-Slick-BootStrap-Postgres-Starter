/***********************************************************************************************
 * User Configuration.
 **********************************************************************************************/
/** Map relative paths to URLs. */
const map: any = {
  'angular2-jwt': 'vendor/angular2-jwt/angular2-jwt.js',
  'moment': 'vendor/moment/min/moment.min.js',
  'ng2-bootstrap': 'vendor/ng2-bootstrap/ng2-bootstrap'
};

/** User packages configuration. */
const packages: any = {
// ng2-bootstrap packages
  'vendor/ng2-bootstrap': {
    defaultExtension: 'js'
  }
};

////////////////////////////////////////////////////////////////////////////////////////////////
/***********************************************************************************************
 * Everything underneath this line is managed by the CLI.
 **********************************************************************************************/
const barrels: string[] = [
  // Angular specific barrels.
  '@angular/core',
  '@angular/common',
  '@angular/compiler',
  '@angular/http',
  '@angular/router',
  '@angular/platform-browser',
  '@angular/platform-browser-dynamic',
  '@angular/platform-browser',
  '@angular/platform-browser-dynamic',
  '@angular/forms',

  // Thirdparty barrels.
  'rxjs',
  'ng2-bootstrap',

  // App specific barrels.
  'app',
  'app/shared',
  'app/login-screen',
  'app/admin-dashboard',
  'app/app-window-component',
  'app/app-window',
  'app/admin-user-list',
  'app/event-list',
  'app/home',
  'app/admin-create-user',
  'app/site-heading',
  /** @cli-barrel */
];

const cliSystemConfigPackages: any = {};
barrels.forEach((barrelName: string) => {
  cliSystemConfigPackages[barrelName] = { main: 'index' };
});

/** Type declaration for ambient System. */
declare var System: any;

// Apply the CLI SystemJS configuration.
System.config({
  map: {
    '@angular': 'vendor/@angular',
    '@angular/forms': 'vendor/@angular/forms',
    'rxjs': 'vendor/rxjs',
    'main': 'main.js'
  },
  packages: cliSystemConfigPackages
});

// Apply the user's configuration.
System.config({ map, packages });
