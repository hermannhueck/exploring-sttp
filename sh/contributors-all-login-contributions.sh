#!/bin/sh
#
# contributors-all-complete.sh
#
# Get login and contributions of all contributors to a GitHub project.

user=lampepfl
repo=dotty
uri="https://api.github.com/repos/$user/$repo/contributors"

curl -s $uri | jq -c '.[] | {login: .login, contributions: .contributions}'
