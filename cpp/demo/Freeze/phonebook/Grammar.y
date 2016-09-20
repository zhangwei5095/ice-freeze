%{

// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#include <IceUtil/PushDisableWarnings.h>
#include <Parser.h>

#ifdef _MSC_VER
// I get these warnings from some bison versions:
// warning C4102: 'yyoverflowlab' : unreferenced label
#   pragma warning( disable : 4102 )
// warning C4065: switch statement contains 'default' but no 'case' labels
#   pragma warning( disable : 4065 )
// warning C4127: conditional expression is constant
#   pragma warning( disable : 4127 )
// warning C4244: '=' : conversion from 'int' to 'yytype_int16', possible loss of data
#   pragma warning( disable : 4244 )
// warning C4702: unreachable code
#   pragma warning( disable : 4702 )
#endif

using namespace std;
using namespace Ice;

void
yyerror(const char* s)
{
    parser->error(s);
}

%}

%pure_parser

%token TOK_HELP
%token TOK_EXIT
%token TOK_ADD_CONTACTS
%token TOK_FIND_CONTACTS
%token TOK_NEXT_FOUND_CONTACT
%token TOK_PRINT_CURRENT
%token TOK_SET_CURRENT_NAME
%token TOK_SET_CURRENT_ADDRESS
%token TOK_SET_CURRENT_PHONE
%token TOK_REMOVE_CURRENT
%token TOK_SET_EVICTOR_SIZE
%token TOK_SHUTDOWN
%token TOK_STRING

%%

// ----------------------------------------------------------------------
start
// ----------------------------------------------------------------------
: commands
{
}
|
{
}
;

// ----------------------------------------------------------------------
commands
// ----------------------------------------------------------------------
: commands command
{
}
| command
{
}
;

// ----------------------------------------------------------------------
command
// ----------------------------------------------------------------------
: TOK_HELP ';'
{
    parser->usage();
}
| TOK_EXIT ';'
{
    return 0;
}
| TOK_ADD_CONTACTS strings ';'
{
    parser->addContacts($2);
}
| TOK_FIND_CONTACTS strings ';'
{
    parser->findContacts($2);
}
| TOK_NEXT_FOUND_CONTACT ';'
{
    parser->nextFoundContact();
}
| TOK_PRINT_CURRENT ';'
{
    parser->printCurrent();
}
| TOK_SET_CURRENT_NAME strings ';'
{
    parser->setCurrentName($2);
}
| TOK_SET_CURRENT_ADDRESS strings ';'
{
    parser->setCurrentAddress($2);
}
| TOK_SET_CURRENT_PHONE strings ';'
{
    parser->setCurrentPhone($2);
}
| TOK_REMOVE_CURRENT ';'
{
    parser->removeCurrent();
}
| TOK_SET_EVICTOR_SIZE strings ';'
{
    parser->setEvictorSize($2);
}
| TOK_SHUTDOWN ';'
{
    parser->shutdown();
}
| error ';'
{
    yyerrok;
}
| ';'
{
}
;

// ----------------------------------------------------------------------
strings
// ----------------------------------------------------------------------
: TOK_STRING strings
{
    $$ = $2;
    $$.push_front($1.front());
}
| TOK_STRING
{
    $$ = $1;
}
;

%%

#include <IceUtil/PopDisableWarnings.h>
