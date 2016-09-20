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

void
yyerror(const char* s)
{
    parser->error(s);
}

%}

%pure_parser

%token TOK_HELP
%token TOK_EXIT
%token TOK_STRING
%token TOK_LIST
%token TOK_LIST_RECURSIVE
%token TOK_CREATE_FILE
%token TOK_CREATE_DIR
%token TOK_PWD
%token TOK_CD
%token TOK_CAT
%token TOK_WRITE
%token TOK_RM

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
| TOK_LIST
{
    parser->list(false);
}
| TOK_LIST_RECURSIVE
{
    parser->list(true);
}
| TOK_CREATE_FILE strings
{
    parser->createFile($2);
}
| TOK_CREATE_DIR strings
{
    parser->createDir($2);
}
| TOK_PWD
{
    parser->pwd();
}
| TOK_CD
{
    parser->cd("/");
}
| TOK_CD strings
{
    parser->cd($2.front());
}
| TOK_CAT TOK_STRING
{
    parser->cat($2.front());
}
| TOK_WRITE strings
{
    parser->write($2);
}
| TOK_RM strings
{
    parser->destroy($2);
}
| error ';'
{
    parser->usage();
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

#include <IceUtil/PushDisableWarnings.h>
