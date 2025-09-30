{
  description = "flake java gnu git";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
  };

  outputs = { self, nixpkgs }: {
    devShells.x86_64-linux.default = nixpkgs.legacyPackages.x86_64-linux.mkShell {
      packages = with nixpkgs.legacyPackages.x86_64-linux; [
        pkgs.gnumake
        pkgs.git
        pkgs.zulu21
      ];
    };
  };
}
